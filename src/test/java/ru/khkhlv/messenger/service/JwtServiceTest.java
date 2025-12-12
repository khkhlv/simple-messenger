package ru.khkhlv.messenger.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.khkhlv.messenger.configuration.JwtService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private TestableJwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new TestableJwtService();
    }

    @Test
    void extractUserId_ShouldReturnUsername_WhenTokenIsValid() {
        // Given
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .build();

        String token = jwtService.generateToken(userDetails);

        // When
        String subject = jwtService.extractUserId(token);

        // Then
        assertEquals("test@example.com", subject);
    }

    @Test
    void generateToken_ShouldReturnValidToken_WhenUserDetailsProvided() {
        // Given
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .build();

        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT состоит из 3 частей
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValidAndNotExpired() {
        // Given
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .build();

        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        // Given
        UserDetails userDetails1 = User.builder()
                .username("test1@example.com")
                .password("password")
                .build();

        UserDetails userDetails2 = User.builder()
                .username("test2@example.com")
                .password("password")
                .build();

        String token = jwtService.generateToken(userDetails1);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails2);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsExpired() {
        // Given
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .build();

        String token = jwtService.generateTokenWithExpiration(userDetails, new Date(System.currentTimeMillis() - 1000));

        // When & Then
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUserId(token));
    }

    @Test
    void extractAllClaims_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> jwtService.extractUserId(invalidToken));
    }


    // Для возможности вызова метода в тесте
    private static class TestableJwtService extends JwtService {
        public String generateTokenWithExpiration(UserDetails userDetails, Date expiration) {
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expiration)
                    .signWith(getSignInKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                    .compact();
        }
    }
}
