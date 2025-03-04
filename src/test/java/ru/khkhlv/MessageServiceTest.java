package ru.khkhlv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.repository.MessageRepo;
import ru.khkhlv.messenger.service.MessageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageRepo messageRepository;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
    }

    @Test
    void testSendMessage() {
        // Подготовка данных
        Message message = new Message();
        message.setSender("Alice");
        message.setRecipient("Bob");
        message.setContent("Hello, Bob!");
        message.setTimestamp(LocalDateTime.now());

        // Мокирование репозитория
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // Вызов метода
        Message result = messageService.sendMessage("Alice", "Bob", "Hello, Bob!");

        // Проверка результата
        assertEquals("Alice", result.getSender());
        assertEquals("Bob", result.getRecipient());
        assertEquals("Hello, Bob!", result.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testGetMessagesForUser() {
        // Подготовка данных
        Message message1 = new Message();
        message1.setSender("Alice");
        message1.setRecipient("Bob");
        message1.setContent("Hello, Bob!");

        Message message2 = new Message();
        message2.setSender("Charlie");
        message2.setRecipient("Bob");
        message2.setContent("Hi, Bob!");

        List<Message> messages = Arrays.asList(message1, message2);

        // Мокирование репозитория
        when(messageRepository.findByRecipientOrderByTimestampAsc("Bob")).thenReturn(messages);

        // Вызов метода
        List<Message> result = messageService.getMessagesForUser("Bob");

        // Проверка результата
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getSender());
        assertEquals("Charlie", result.get(1).getSender());
        verify(messageRepository, times(1)).findByRecipientOrderByTimestampAsc("Bob");
    }

    @Test
    void testDeleteMessage() {
        // Мокирование репозитория
        when(messageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(messageRepository).deleteById(1L);

        // Вызов метода
        messageService.deleteMessage(1L);

        // Проверка результата
        verify(messageRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetMessageById() {
        // Подготовка данных
        Message message = new Message();
        message.setId(1L);
        message.setSender("Alice");
        message.setRecipient("Bob");
        message.setContent("Hello, Bob!");

        // Мокирование репозитория
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Вызов метода
        Message result = messageService.getMessageById(1L);

        // Проверка результата
        assertEquals("Alice", result.getSender());
        assertEquals("Bob", result.getRecipient());
        assertEquals("Hello, Bob!", result.getContent());
        verify(messageRepository, times(1)).findById(1L);
    }
}
