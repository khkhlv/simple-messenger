package ru.khkhlv.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.khkhlv.messenger.dto.ChatDto;
import ru.khkhlv.messenger.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatDto>> getUserChats() {
        return ResponseEntity.ok(chatService.getUserChats());
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(@RequestBody List<Long> participantIds) {
        return ResponseEntity.ok(chatService.createChat(participantIds));
    }
}
