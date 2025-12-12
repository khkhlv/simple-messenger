package ru.khkhlv.messenger.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.khkhlv.messenger.dto.MessageDto;
import ru.khkhlv.messenger.service.MessageService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(messageController).build();
    }

    @Test
    @WithMockUser
    void getMessages_ShouldReturnMessages_WhenChatExists() throws Exception {
        Long chatId = 1L;
        MessageDto messageDto = new MessageDto(1L, "user1", "Hello", LocalDateTime.now());

        when(messageService.getMessages(eq(chatId))).thenReturn(List.of(messageDto));

        mockMvc.perform(get("/chats/{chatId}/messages", chatId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello"));
    }

    @Test
    @WithMockUser
    void sendMessage_ShouldReturnMessage_WhenValidRequest() throws Exception {
        Long chatId = 1L;
        String content = "Hello!";
        MessageDto messageDto = new MessageDto(1L, "user1", content, LocalDateTime.now());

        when(messageService.sendMessage(eq(chatId), any(String.class))).thenReturn(messageDto);

        mockMvc.perform(post("/chats/{chatId}/messages", chatId)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    @WithMockUser
    void deleteMessage_ShouldReturnNoContent_WhenValidRequest() throws Exception {
        Long chatId = 1L;
        Long messageId = 1L;

        mockMvc.perform(delete("/chats/{chatId}/messages/{messageId}", chatId, messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void searchMessages_ShouldReturnMatchingMessages_WhenQueryProvided() throws Exception {
        Long chatId = 1L;
        String query = "hello";
        MessageDto messageDto = new MessageDto(1L, "user1", "Hello world", LocalDateTime.now());

        when(messageService.searchMessages(eq(chatId), eq(query))).thenReturn(List.of(messageDto));

        mockMvc.perform(get("/chats/{chatId}/messages/search", chatId)
                        .param("query", query)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello world"));
    }
}
