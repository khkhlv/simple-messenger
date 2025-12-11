package ru.khkhlv.messenger.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.khkhlv.messenger.configuration.JwtAuthenticationFilter;
import ru.khkhlv.messenger.configuration.JwtService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext(); // ✅ Очищаем перед каждым тестом
        jwtAuthFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Test
    void doFilterInternal_ShouldSetAuthentication_WhenTokenIsValid() throws ServletException, IOException {
        // Given
        String token = "Bearer valid-token";
        String username = "test@example.com";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(jwtService.extractUserId("valid-token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", token);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(), any());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String token = "Bearer invalid-token";
        String username = "test@example.com";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(jwtService.extractUserId("invalid-token")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalid-token", userDetails)).thenReturn(false); // ✅

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", token);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set when token is invalid");
    }

    @Test
    void doFilterInternal_ShouldNotSetAuthentication_WhenTokenExpired() throws ServletException, IOException {
        // Given
        String token = "Bearer expired-token";
        String username = "test@example.com";

        when(jwtService.extractUserId("expired-token")).thenThrow(new ExpiredJwtException(null, null, "Expired"));

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("Authorization", token);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(any(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set when token is expired");
    }
}