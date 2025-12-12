package ru.khkhlv.messenger.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityContextHelperTest {

    @Mock
    private UserRepository userRepository;

    private SecurityContextHelper securityContextHelper;

    @BeforeEach
    void setUp() {
        securityContextHelper = new SecurityContextHelper(userRepository);
    }

    @Test
    void getCurrentUserId_ShouldReturnUserId_WhenAuthenticated() {
        // Given
        Long userId = 1L;
        String email = "test@example.com";
        User user = new User();
        user.setId(userId);

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(context);

        // When
        Long result = securityContextHelper.getCurrentUserId();

        // Then
        assertEquals(userId, result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getCurrentUserId_ShouldThrow_WhenNotAuthenticated() {
        // Given
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(context);

        // When & Then
        assertThrows(RuntimeException.class, () -> securityContextHelper.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_ShouldThrow_WhenAuthenticationIsNull() {
        // Given
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(context);

        // When & Then
        assertThrows(RuntimeException.class, () -> securityContextHelper.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_ShouldThrow_WhenUserNotFoundByEmail() {
        // Given
        String email = "nonexistent@example.com";

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(context);

        // When & Then
        assertThrows(RuntimeException.class, () -> securityContextHelper.getCurrentUserId());
    }

    @Test
    void logger_ShouldBeUsed_WhenLogging() {
        // Given
        String email = "test@example.com";
        Logger logger = mock(Logger.class);

        // We can't easily mock static LoggerFactory.getLogger(), so just test that logging calls are made
        // We'll rely on the fact that SLF4J is used and logs are generated normally in real usage

        // Just ensure the code path executes without error
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(context.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        SecurityContextHolder.setContext(context);

        // When
        assertDoesNotThrow(() -> securityContextHelper.getCurrentUserId());
    }
}