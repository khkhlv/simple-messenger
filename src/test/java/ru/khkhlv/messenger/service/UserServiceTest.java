package ru.khkhlv.messenger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khkhlv.messenger.dto.UserDto;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void searchUsers_ShouldReturnUserDtos_WhenQueryMatchesUsername() {
        // Given
        String query = "test";
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("another_test_user");
        user2.setEmail("another@example.com");

        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query))
                .thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserDto> result = userService.searchUsers(query);

        // Then
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).username());
        assertEquals("test@example.com", result.get(0).email());
        assertEquals("another_test_user", result.get(1).username());
        assertEquals("another@example.com", result.get(1).email());
        verify(userRepository).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    @Test
    void searchUsers_ShouldReturnUserDtos_WhenQueryMatchesEmail() {
        // Given
        String query = "example";
        User user = new User();
        user.setId(1L);
        user.setUsername("someuser");
        user.setEmail("test@example.com");

        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query))
                .thenReturn(List.of(user));

        // When
        List<UserDto> result = userService.searchUsers(query);

        // Then
        assertEquals(1, result.size());
        assertEquals("someuser", result.get(0).username());
        assertEquals("test@example.com", result.get(0).email());
    }

    @Test
    void searchUsers_ShouldReturnEmptyList_WhenNoMatchesFound() {
        // Given
        String query = "nonexistent";
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query))
                .thenReturn(List.of());

        // When
        List<UserDto> result = userService.searchUsers(query);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    @Test
    void searchUsers_ShouldReturnEmptyList_WhenQueryIsEmpty() {
        // Given
        String query = "";
        // findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase может возвращать всех, если query = ""
        // В реальности ты можешь обработать это в UserService, например:
        // if (query.isBlank()) return List.of();

        // Но если ты хочешь, чтобы даже пустой запрос вызывал репозиторий:
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(eq(query), eq(query)))
                .thenReturn(List.of());

        // When
        List<UserDto> result = userService.searchUsers(query);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
    }

    @Test
    void searchUsers_ShouldHandleNullQueryGracefully() {
        // Given
        String query = null;

        // Мокаем вызов с null
        when(userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(isNull(), isNull()))
                .thenReturn(List.of());

        // When
        List<UserDto> result = userService.searchUsers(query);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(isNull(), isNull());
    }
}
