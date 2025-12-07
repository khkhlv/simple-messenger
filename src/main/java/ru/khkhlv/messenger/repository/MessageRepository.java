package ru.khkhlv.messenger.repository;

import ru.khkhlv.messenger.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdAndDeletedFalseOrderByTimestampAsc(Long chatId);
    List<Message> findByChatIdAndContentContainingIgnoreCaseAndDeletedFalse(Long chatId, String keyword);
}