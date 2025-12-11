package ru.khkhlv.messenger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.khkhlv.messenger.dto.MessageDto;
import ru.khkhlv.messenger.model.Chat;
import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.ChatRepository;
import ru.khkhlv.messenger.repository.MessageRepository;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContextHelper securityContextHelper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, chatRepository, userRepository, securityContextHelper, messagingTemplate);
    }

    @Test
    void sendMessage_ShouldSaveMessageAndBroadcast() {
        Long chatId = 1L;
        Long userId = 1L;
        String content = "Hello";

        User sender = new User();
        sender.setId(userId);
        Chat chat = new Chat();
        chat.setId(chatId);
        Set<User> participants = new HashSet<>();
        participants.add(sender);
        chat.setParticipants(participants);

        when(securityContextHelper.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(sender));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> i.getArgument(0));

        MessageDto result = messageService.sendMessage(chatId, content);

        assertNotNull(result);
        assertEquals(content, result.content());
    }

    @Test
    void deleteMessage_ShouldSetDeletedFlag() {
        // Given
        Long chatId = 1L;
        Long userId = 1L; // текущий пользователь
        Long messageId = 1L;

        User sender = new User();
        sender.setId(userId);

        Chat chat = new Chat();
        chat.setId(chatId);
        Set<User> participants = new HashSet<>();
        participants.add(sender); // текущий пользователь в чате
        chat.setParticipants(participants);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);
        message.setChat(chat); // важно: сообщение принадлежит чату

        // Моки
        when(securityContextHelper.getCurrentUserId()).thenReturn(userId);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat)); // ← добавлено
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        // When
        messageService.deleteMessage(chatId, messageId);

        // Then
        assertTrue(message.isDeleted());
        verify(messageRepository).save(message);
    }

    @Test
    void getMessages_ShouldReturnNonDeletedMessages() {
        // Given
        Long chatId = 1L;
        Long userId = 1L; // текущий пользователь
        Message msg = new Message();
        msg.setDeleted(false);
        msg.setContent("Hello");

        User sender = new User();
        sender.setId(userId);
        sender.setUsername("testuser"); // важно!
        msg.setSender(sender); // ✅ Установи отправителя

        Chat chat = new Chat();
        chat.setId(chatId);
        Set<User> participants = new HashSet<>();
        participants.add(sender);
        chat.setParticipants(participants);

        // Моки
        when(securityContextHelper.getCurrentUserId()).thenReturn(userId);
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.findByChatIdAndDeletedFalseOrderByTimestampAsc(chatId)).thenReturn(List.of(msg));

        // When
        List<MessageDto> result = messageService.getMessages(chatId);

        // Then
        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).content());
        assertEquals("testuser", result.get(0).sender());
    }
}
