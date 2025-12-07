package ru.khkhlv.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khkhlv.messenger.dto.MessageDto;
import ru.khkhlv.messenger.model.Chat;
import ru.khkhlv.messenger.model.Message;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.ChatRepository;
import ru.khkhlv.messenger.repository.MessageRepository;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    public List<MessageDto> getMessages(Long chatId) {
        validateUserInChat(chatId);
        return messageRepository.findByChatIdAndDeletedFalseOrderByTimestampAsc(chatId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MessageDto sendMessage(Long chatId, String content) {
        validateUserInChat(chatId);
        Long userId = securityContextHelper.getCurrentUserId();
        User sender = userRepository.findById(userId).orElseThrow();
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setChat(chat);
        message.setContent(content);
        message = messageRepository.save(message);
        return toDto(message);
    }

    public void deleteMessage(Long chatId, Long messageId) {
        validateUserInChat(chatId);
        Long userId = securityContextHelper.getCurrentUserId();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Not your message");
        }
        message.setDeleted(true);
        messageRepository.save(message);
    }

    public List<MessageDto> searchMessages(Long chatId, String query) {
        validateUserInChat(chatId);
        return messageRepository.findByChatIdAndContentContainingIgnoreCaseAndDeletedFalse(chatId, query).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void validateUserInChat(Long chatId) {
        Long userId = securityContextHelper.getCurrentUserId();
        boolean isInChat = chatRepository.findById(chatId)
                .map(chat -> chat.getParticipants().stream().anyMatch(u -> u.getId().equals(userId)))
                .orElse(false);
        if (!isInChat) {
            throw new RuntimeException("Access denied");
        }
    }

    private MessageDto toDto(Message m) {
        return new MessageDto(
                m.getId(),
                m.getSender().getUsername(),
                m.getContent(),
                m.getTimestamp()
        );
    }
}