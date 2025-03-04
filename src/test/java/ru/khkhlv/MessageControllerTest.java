package ru.khkhlv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.khkhlv.messenger.controller.MessageController;
import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.service.MessageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MessageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
        mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    void testSendMessage() throws Exception {
        // Подготовка данных
        Message message = new Message();
        message.setSender("Alice");
        message.setRecipient("Bob");
        message.setContent("Hello, Bob!");
        message.setTimestamp(LocalDateTime.now());

        // Мокирование сервиса
        when(messageService.sendMessage("Alice", "Bob", "Hello, Bob!")).thenReturn(message);

        // Вызов метода и проверка результата
        mockMvc.perform(post("/messages/send")
                        .param("sender", "Alice")
                        .param("recipient", "Bob")
                        .param("content", "Hello, Bob!")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender").value("Alice"))
                .andExpect(jsonPath("$.recipient").value("Bob"))
                .andExpect(jsonPath("$.content").value("Hello, Bob!"));
    }

    @Test
    void testGetMessagesForUser() throws Exception {
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

        // Мокирование сервиса
        when(messageService.getMessagesForUser("Bob")).thenReturn(messages);

        // Вызов метода и проверка результата
        mockMvc.perform(get("/messages/Bob")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender").value("Alice"))
                .andExpect(jsonPath("$[1].sender").value("Charlie"));
    }

    @Test
    void testDeleteMessage() throws Exception {
        // Мокирование сервиса
        doNothing().when(messageService).deleteMessage(1L);

        // Вызов метода и проверка результата
        mockMvc.perform(delete("/messages/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Message with ID 1 has been deleted."));
    }
}
