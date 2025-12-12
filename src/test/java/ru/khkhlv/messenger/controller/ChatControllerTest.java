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
import ru.khkhlv.messenger.dto.ChatDto;
import ru.khkhlv.messenger.service.ChatService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
    }

    @Test
    @WithMockUser
    void getUserChats_ShouldReturnChats_WhenAuthenticated() throws Exception {
        ChatDto chatDto = new ChatDto(1L, List.of("user1", "user2"));

        when(chatService.getUserChats()).thenReturn(List.of(chatDto));

        mockMvc.perform(get("/chats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].participants").isArray());
    }

    @Test
    @WithMockUser
    void createChat_ShouldReturnChat_WhenValidRequest() throws Exception {
        List<Long> participantIds = List.of(2L);
        ChatDto chatDto = new ChatDto(1L, List.of("user1", "user2"));

        when(chatService.createChat(any(List.class))).thenReturn(chatDto);

        mockMvc.perform(post("/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(participantIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    private String asJsonString(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}