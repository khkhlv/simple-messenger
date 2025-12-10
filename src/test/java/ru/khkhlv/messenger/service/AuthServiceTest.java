package ru.khkhlv.messenger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khkhlv.messenger.dto.RegisterRequest;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldSaveUser_WhenEmailIsUnique() {
        RegisterRequest request = new RegisterRequest("user", "user@example.com", "pass");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest("user", "user@example.com", "pass");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
