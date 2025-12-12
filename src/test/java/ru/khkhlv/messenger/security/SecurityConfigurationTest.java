package ru.khkhlv.messenger.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.khkhlv.messenger.configuration.JwtAuthenticationFilter;
import ru.khkhlv.messenger.configuration.SecurityConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigurationTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        securityConfiguration = new SecurityConfiguration(jwtAuthFilter, userDetailsService);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // When
        PasswordEncoder encoder = securityConfiguration.passwordEncoder();

        // Then
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        // When
        AuthenticationProvider provider = securityConfiguration.authenticationProvider();

        // Then
        assertNotNull(provider);
        // Проверим, что setUserDetailsService и setPasswordEncoder вызваны
        // Это можно сделать, если будем использовать spy, но в данном случае просто проверим тип
        assertInstanceOf(org.springframework.security.authentication.dao.DaoAuthenticationProvider.class, provider);
    }

    @Test
    void authenticationManager_ShouldReturnAuthenticationManager() throws Exception {
        // Given
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        // When
        AuthenticationManager manager = securityConfiguration.authenticationManager(authenticationConfiguration);

        // Then
        assertNotNull(manager);
        assertEquals(mockAuthManager, manager);
    }
}
