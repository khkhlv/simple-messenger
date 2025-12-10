package ru.khkhlv.messenger.integretion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.khkhlv.messenger.dto.LoginRequest;
import ru.khkhlv.messenger.dto.RegisterRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatControllerIT extends BaseIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setupUsers() {
        // Register user1
        ResponseEntity<Void> reg1 = restTemplate.postForEntity("http://localhost:" + port + "/auth/register",
                new RegisterRequest("user1", "u1@test.com", "pass"), Void.class);
        assertThat(reg1.getStatusCode()).isEqualTo(HttpStatus.OK); // ✅ Проверь статус

        // Register user2
        ResponseEntity<Void> reg2 = restTemplate.postForEntity("http://localhost:" + port + "/auth/register",
                new RegisterRequest("user2", "u2@test.com", "pass"), Void.class);
        assertThat(reg2.getStatusCode()).isEqualTo(HttpStatus.OK); // ✅ Проверь статус

        // Login user1
        LoginRequest login1 = new LoginRequest("u1@test.com", "pass");
        ResponseEntity<AuthControllerIT.AuthResponse> resp1 = restTemplate.postForEntity("http://localhost:" + port + "/auth/login", login1, AuthControllerIT.AuthResponse.class);

        // ✅ Проверь ответ
        assertThat(resp1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp1.getBody()).isNotNull();
        user1Token = resp1.getBody().token;

        // Login user2
        LoginRequest login2 = new LoginRequest("u2@test.com", "pass");
        ResponseEntity<AuthControllerIT.AuthResponse> resp2 = restTemplate.postForEntity("http://localhost:" + port + "/auth/login", login2, AuthControllerIT.AuthResponse.class);

        // ✅ Проверь ответ
        assertThat(resp2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp2.getBody()).isNotNull();
        user2Token = resp2.getBody().token;
    }

    @Test
    void createChat_ShouldReturnChatWithTwoParticipants() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user1Token);
        HttpEntity<List<Long>> request = new HttpEntity<>(List.of(2L), headers);

        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/chats", request, ChatResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().participants).contains("user1", "user2");
    }

    public static class ChatResponse {
        public Long id;
        public List<String> participants;
    }
}