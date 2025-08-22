package com.example.backend.repository.message;


import com.example.backend.entity.message.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.user1Id = :user1 AND c.user2Id = :user2) " +
            "OR (c.user1Id = :user2 AND c.user2Id = :user1)")
    Optional<Conversation> findByUsers(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query("SELECT c FROM Conversation c WHERE c.user1Id = :userId OR c.user2Id = :userId " +
            "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findUserConversations(@Param("userId") Long userId);
}
