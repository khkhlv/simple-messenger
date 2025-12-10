package ru.khkhlv.messenger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.khkhlv.messenger.dto.ChatDto;
import ru.khkhlv.messenger.model.Chat;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.ChatRepository;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// service/ChatServiceTest.java
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private ChatService chatService;

    @Test
    void createChat_ShouldSaveChatWithParticipants() {
        // Given
        Chat savedChat = new Chat();
        savedChat.setId(1L);
        Set<User> participants = Set.of(new User(1L, "user1", "u1@test.com", "pass"),
                new User(2L, "user2", "u2@test.com", "pass"));
        savedChat.setParticipants(participants);

        when(securityContextHelper.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "user1", "u1@test.com", "pass")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "user2", "u2@test.com", "pass")));
        when(chatRepository.save(any(Chat.class))).thenReturn(savedChat); // <-- Вот это важно!

        // When
        ChatDto result = chatService.createChat(List.of(2L));

        // Then
        verify(chatRepository).save(any(Chat.class));
        assertThat(result.participants()).hasSize(2);
    }
}
