package com.example.backend.event;

import com.example.backend.entity.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishMessageSent(Message message, String senderName) {
        MessageEvent event = MessageEvent.builder()
                .messageId(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderName(senderName)
                .content(message.getContent())
                .messageType(message.getMessageType())
                .timestamp(message.getTimestamp())
                .build();

        eventPublisher.publishEvent(event);
        log.debug("Published message event for message ID: {}", message.getId());
    }
}
