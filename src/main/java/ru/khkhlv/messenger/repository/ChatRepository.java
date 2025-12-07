package ru.khkhlv.messenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.khkhlv.messenger.model.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c JOIN c.participants u WHERE u.id = :userId")
    List<Chat> findByUserId(Long userId);
}
