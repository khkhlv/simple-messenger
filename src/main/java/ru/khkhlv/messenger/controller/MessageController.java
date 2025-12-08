package ru.khkhlv.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import ru.khkhlv.messenger.dto.MessageDto;
import ru.khkhlv.messenger.service.MessageService;

import java.util.List;

@RestController
@RequestMapping("/chats/{chatId}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.getMessages(chatId));
    }

    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@PathVariable Long chatId, @RequestBody String content) {
        return ResponseEntity.ok(messageService.sendMessage(chatId, content.trim()));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long chatId, @PathVariable Long messageId) {
        messageService.deleteMessage(chatId, messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<MessageDto>> searchMessages(
            @PathVariable Long chatId,
            @RequestParam String query) {
        return ResponseEntity.ok(messageService.searchMessages(chatId, query));
    }

    @MessageMapping("/chat/{chatId}/send") // вызывается при /app/chat/123/send
    @SendTo("/topic/chat/{chatId}")       // автоматически рассылает ответ
    public void handleChatMessage(@DestinationVariable Long chatId, String content) {}
}
