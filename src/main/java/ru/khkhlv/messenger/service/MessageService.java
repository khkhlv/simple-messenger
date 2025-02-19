package ru.khkhlv.messenger.service;

import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    public Message sendMessage(String sender, String recipient, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return messageRepo.save(message);
    }

    public List<Message> getMessagesForUser(String recipient) {
        return messageRepo.findByRecipientOrderByTimestampAsc(recipient);
    }

    public void deleteMessage(Long id) {
        messageRepo.deleteById(id);
    }

    public Message getMessageById(Long id) {
        return messageRepo.getReferenceById(id);
    }
}