package ru.khkhlv.messenger.repository;

import ru.khkhlv.messenger.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByRecipientOrderByTimestampAsc(String recipient);

    List<Message> findByContentContainingIgnoreCase(String keyword);
}