package ru.khkhlv.messenger.integretion;

import ru.khkhlv.messenger.dto.LoginRequest;
import ru.khkhlv.messenger.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthControllerIT extends BaseIntegrationTest {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void registerAndLogin_ShouldReturnToken() {
        // Register
        RegisterRequest register = new RegisterRequest("user1", "user1@example.com", "pass123");
        ResponseEntity<Void> regResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/register", register, Void.class);
        assertThat(regResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Login
        LoginRequest login = new LoginRequest("user1@example.com", "pass123");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/auth/login", login, AuthResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token).isNotBlank();
    }

    public static class AuthResponse {
        public String token;
    }
}