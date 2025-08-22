package com.example.backend.dto.message.request;

import com.example.backend.entity.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessage {
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType messageType;
    private String action; // SEND_MESSAGE, MARK_READ, TYPING, etc.
}
