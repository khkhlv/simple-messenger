package ru.khkhlv.messenger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khkhlv.messenger.configuration.JwtService;
import ru.khkhlv.messenger.dto.LoginRequest;
import ru.khkhlv.messenger.dto.RegisterRequest;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService; // ✅ Мокаем CustomUserDetailsService

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, userDetailsService);
    }

    @Test
    void register_ShouldSaveUser_WhenEmailIsUnique() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrow_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-token");

        String token = authService.login(request);

        assertEquals("mocked-token", token);
    }

    @Test
    void login_ShouldThrow_WhenEmailNotFound() {
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");
        // Предполагаем, что CustomUserDetailsService бросает UsernameNotFoundException
        when(userDetailsService.loadUserByUsername("nonexistent@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }
}