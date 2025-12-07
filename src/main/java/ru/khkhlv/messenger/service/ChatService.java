package ru.khkhlv.messenger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khkhlv.messenger.dto.ChatDto;
import ru.khkhlv.messenger.model.Chat;
import ru.khkhlv.messenger.model.User;
import ru.khkhlv.messenger.repository.ChatRepository;
import ru.khkhlv.messenger.repository.UserRepository;
import ru.khkhlv.messenger.util.SecurityContextHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SecurityContextHelper securityContextHelper;

    public List<ChatDto> getUserChats() {
        Long userId = securityContextHelper.getCurrentUserId();
        return chatRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ChatDto createChat(List<Long> participantIds) {
        Long currentUserId = securityContextHelper.getCurrentUserId();
        Set<User> participants = new HashSet<>();
        participants.add(userRepository.findById(currentUserId).orElseThrow());
        for (Long id : participantIds) {
            participants.add(userRepository.findById(id).orElseThrow());
        }

        Chat chat = new Chat();
        chat.setParticipants(participants);
        chat = chatRepository.save(chat);
        return toDto(chat);
    }

    private ChatDto toDto(Chat chat) {
        List<String> usernames = chat.getParticipants().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return new ChatDto(chat.getId(), usernames);
    }
}