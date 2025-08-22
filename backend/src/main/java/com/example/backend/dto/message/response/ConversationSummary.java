package com.example.backend.dto.message.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ConversationSummary {
    private Long conversationId;
    private Long otherUserId;
    private String otherUserName;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
}
