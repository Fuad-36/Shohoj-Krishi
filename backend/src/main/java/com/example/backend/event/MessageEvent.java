package com.example.backend.event;

import com.example.backend.entity.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class MessageEvent {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
}
