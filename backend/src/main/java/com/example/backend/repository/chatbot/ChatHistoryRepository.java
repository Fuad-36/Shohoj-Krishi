package com.example.backend.repository.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import com.example.backend.entity.chatbot.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    @Query("SELECT ch FROM ChatHistory ch WHERE ch.userId = :userId ORDER BY ch.timestamp DESC")
    Page<ChatHistory> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ch FROM ChatHistory ch WHERE ch.userId = :userId AND ch.chatType = :chatType ORDER BY ch.timestamp DESC")
    List<ChatHistory> findRecentChatByType(@Param("userId") Long userId, @Param("chatType") ChatType chatType, Pageable pageable);
}
