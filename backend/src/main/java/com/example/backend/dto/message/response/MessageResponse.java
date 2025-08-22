package com.example.backend.dto.message.response;

import com.example.backend.entity.message.Message;
import com.example.backend.entity.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
    private Boolean isRead;
    private String senderName;

    // Constructor from Message entity
    public MessageResponse(Message message, String senderName) {
        this.messageId = message.getId();
        this.senderId = message.getSenderId();
        this.receiverId = message.getReceiverId();
        this.content = message.getContent();
        this.messageType = message.getMessageType();
        this.timestamp = message.getTimestamp();
        this.isRead = message.getIsRead();
        this.senderName = senderName;
    }
}
