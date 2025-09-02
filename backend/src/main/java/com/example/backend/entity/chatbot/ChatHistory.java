package com.example.backend.entity.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String userMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String botResponse;

    @Enumerated(EnumType.STRING)
    private ChatType chatType;

    private String language;
    private Boolean wasPersonalized = false; // Whether farmer context was used

    @Column(columnDefinition = "TEXT")
    private String contextUsed; // JSON of farmer context that was used

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
