package ru.khkhlv.messenger.integretion;

import ru.khkhlv.messenger.dto.LoginRequest;
import ru.khkhlv.messenger.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MessageControllerIT extends BaseIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private String user1Token;
    private Long chatId;

    @BeforeEach
    void setupChat() {
        // Register two users
        restTemplate.postForEntity("http://localhost:" + port + "/auth/register",
                new RegisterRequest("user1", "u1@test.com", "pass"), Void.class);
        restTemplate.postForEntity("http://localhost:" + port + "/auth/register",
                new RegisterRequest("user2", "u2@test.com", "pass"), Void.class);

        // Login user1
        LoginRequest login1 = new LoginRequest("u1@test.com", "pass");
        var resp1 = restTemplate.postForEntity("http://localhost:" + port + "/auth/login",
                login1, AuthControllerIT.AuthResponse.class);
        user1Token = resp1.getBody().token;

        // Create chat between user1 (id=1) and user2 (id=2)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user1Token);
        HttpEntity<List<Long>> request = new HttpEntity<>(List.of(2L), headers);
        var chatResp = restTemplate.postForEntity("http://localhost:" + port + "/chats", request, ChatControllerIT.ChatResponse.class);
        chatId = chatResp.getBody().id;
    }

    @Test
    void sendMessage_ShouldReturnMessage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user1Token);
        HttpEntity<String> request = new HttpEntity<>("Hello, world!", headers);

        ResponseEntity<MessageResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/chats/" + chatId + "/messages", request, MessageResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().content).isEqualTo("Hello, world!");
        assertThat(response.getBody().sender).isEqualTo("user1");
    }

    @Test
    void deleteMessage_ShouldSoftDelete() {
        // Send message
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user1Token);
        HttpEntity<String> sendReq = new HttpEntity<>("To be deleted", headers);
        var sendResp = restTemplate.postForEntity(
                "http://localhost:" + port + "/chats/" + chatId + "/messages", sendReq, MessageResponse.class);
        Long messageId = sendResp.getBody().id;

        // Delete message
        HttpEntity<Void> deleteReq = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
                "http://localhost:" + port + "/chats/" + chatId + "/messages/" + messageId,
                HttpMethod.DELETE, deleteReq, Void.class);
        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify it's gone from history
        ResponseEntity<List<MessageResponse>> getResp = restTemplate.exchange(
                "http://localhost:" + port + "/chats/" + chatId + "/messages",
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<MessageResponse>>() {});
        assertThat(getResp.getBody()).noneMatch(m -> m.id.equals(messageId));
    }

    @Test
    void searchMessages_ShouldReturnMatching() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user1Token);
        restTemplate.postForEntity(
                "http://localhost:" + port + "/chats/" + chatId + "/messages",
                new HttpEntity<>("Let's drink coffee", headers), MessageResponse.class);
        restTemplate.postForEntity(
                "http://localhost:" + port + "/chats/" + chatId + "/messages",
                new HttpEntity<>("Tea is better", headers), MessageResponse.class);

        // Search
        ResponseEntity<List<MessageResponse>> searchResp = restTemplate.exchange(
                "http://localhost:" + port + "/chats/" + chatId + "/messages/search?query=coffee",
                HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<MessageResponse>>() {});

        assertThat(searchResp.getBody()).hasSize(1);
        assertThat(searchResp.getBody().get(0).content).contains("coffee");
    }

    @Test
    void cannotAccessOtherUsersChat() {
        // Create second user and chat (not involving user1)
        restTemplate.postForEntity("http://localhost:" + port + "/auth/register",
                new RegisterRequest("user3", "u3@test.com", "pass"), Void.class);
        LoginRequest login3 = new LoginRequest("u3@test.com", "pass");
        var resp3 = restTemplate.postForEntity("http://localhost:" + port + "/auth/login",
                login3, AuthControllerIT.AuthResponse.class);
        String user3Token = resp3.getBody().token;

        // User3 creates chat with user2 (ids: 3 and 2)
        HttpHeaders h3 = new HttpHeaders();
        h3.setBearerAuth(user3Token);
        HttpEntity<List<Long>> req3 = new HttpEntity<>(List.of(2L), h3);
        var chat3Resp = restTemplate.postForEntity("http://localhost:" + port + "/chats", req3, ChatControllerIT.ChatResponse.class);
        Long otherChatId = chat3Resp.getBody().id;

        // User1 tries to access it → should be 403
        HttpHeaders h1 = new HttpHeaders();
        h1.setBearerAuth(user1Token);
        ResponseEntity<?> accessResp = restTemplate.exchange(
                "http://localhost:" + port + "/chats/" + otherChatId + "/messages",
                HttpMethod.GET, new HttpEntity<>(h1), String.class);

        assertThat(accessResp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public static class MessageResponse {
        public Long id;
        public String sender;
        public String content;
        public String timestamp;
    }
}