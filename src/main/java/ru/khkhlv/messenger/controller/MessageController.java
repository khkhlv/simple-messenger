package ru.khkhlv.messenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Message> sendMessage(@RequestParam("sender") String sender,
                                               @RequestParam("recipient") String recipient,
                                               @RequestParam("content") String content) {
        try {
            Message message = messageService.sendMessage(sender, recipient, content);
            return ResponseEntity.ok(message);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{recipient}")
    public List<Message> getMessages(@PathVariable("recipient") String recipient) {
        return messageService.getMessagesForUser(recipient);
    }

    @GetMapping("/message/{id}")
    public Message getMessage(@PathVariable("id") Long id) {
        return messageService.getMessageById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessage(id);
        return "Message with ID " + id + " has been deleted.";
    }

    @GetMapping("/all")
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping("/search")
    public List<Message> searchMessages(@RequestParam("keyword") String keyword) {
        return messageService.searchMessages(keyword);
    }
}
