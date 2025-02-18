package ru.khkhlv.messenger.controller;

import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestParam String sender, @RequestParam String recipient, @RequestParam String content) {
        return messageService.sendMessage(sender, recipient, content);
    }

    @GetMapping("/{recipient}")
    public List<Message> getMessages(@PathVariable String recipient) {
        return messageService.getMessagesForUser(recipient);
    }
}
