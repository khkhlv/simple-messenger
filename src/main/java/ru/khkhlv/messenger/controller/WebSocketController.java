package ru.khkhlv.messenger.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.khkhlv.messenger.service.MessageService;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;

    @MessageMapping("/chat/{chatId}/send") // вызывается при /app/chat/123/send
    @SendTo("/topic/chat/{chatId}")       // автоматически рассылает ответ
    public void handleChatMessage(@DestinationVariable Long chatId, String content) {
        // Тут ты можешь вызвать messageService.sendMessage(chatId, content)
        // Но не возвращай ничего, потому что @SendTo не работает с void
        // Лучше вызывай через REST
    }
}
