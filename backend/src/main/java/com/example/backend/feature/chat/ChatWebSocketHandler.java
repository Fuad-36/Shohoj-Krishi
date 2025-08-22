package com.example.backend.feature.chat;

import com.example.backend.config.WebSocketSessionManager;
import com.example.backend.dto.message.request.WebSocketMessage;
import com.example.backend.dto.message.response.MessageResponse;
import com.example.backend.service.message.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final MessagingService messagingService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessionManager.addSession(userId, session);

            // Send online status to user's contacts
            notifyContactsUserOnline(userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessionManager.removeSession(userId);

            // Send offline status to user's contacts
            notifyContactsUserOffline(userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            WebSocketMessage wsMessage = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);
            Long senderId = getUserIdFromSession(session);

            if (senderId == null || !senderId.equals(wsMessage.getSenderId())) {
                log.warn("Unauthorized message attempt from session");
                return;
            }

            switch (wsMessage.getAction()) {
                case "SEND_MESSAGE" -> handleSendMessage(wsMessage);
                case "MARK_READ" -> handleMarkRead(wsMessage);
                case "TYPING" -> handleTyping(wsMessage);
                case "GET_ONLINE_STATUS" -> handleGetOnlineStatus(wsMessage);
                default -> log.warn("Unknown action: {}", wsMessage.getAction());
            }

        } catch (Exception e) {
            log.error("Error handling WebSocket message: {}", e.getMessage());
        }
    }

    private void handleSendMessage(WebSocketMessage wsMessage) {
        try {
            // Save message to database
            MessageResponse savedMessage = messagingService.sendMessage(
                    wsMessage.getSenderId(),
                    wsMessage.getReceiverId(),
                    wsMessage.getContent(),
                    wsMessage.getMessageType()
            );

            // Create response object
            Map<String, Object> response = Map.of(
                    "action", "NEW_MESSAGE",
                    "message", savedMessage
            );

            // Send to receiver if online
            if (sessionManager.isUserOnline(wsMessage.getReceiverId())) {
                sessionManager.sendMessageToUser(wsMessage.getReceiverId(), response);
            }

            // Send confirmation back to sender
            Map<String, Object> confirmation = Map.of(
                    "action", "MESSAGE_SENT",
                    "message", savedMessage
            );
            sessionManager.sendMessageToUser(wsMessage.getSenderId(), confirmation);

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
        }
    }

    private void handleMarkRead(WebSocketMessage wsMessage) {
        messagingService.markMessagesAsRead(wsMessage.getSenderId(), wsMessage.getReceiverId());

        // Notify sender that messages were read
        Map<String, Object> readNotification = Map.of(
                "action", "MESSAGES_READ",
                "userId", wsMessage.getReceiverId()
        );

        sessionManager.sendMessageToUser(wsMessage.getSenderId(), readNotification);
    }

    private void handleTyping(WebSocketMessage wsMessage) {
        // Send typing indicator to receiver
        Map<String, Object> typingIndicator = Map.of(
                "action", "TYPING",
                "senderId", wsMessage.getSenderId(),
                "isTyping", true
        );

        sessionManager.sendMessageToUser(wsMessage.getReceiverId(), typingIndicator);
    }

    private void handleGetOnlineStatus(WebSocketMessage wsMessage) {
        boolean isOnline = sessionManager.isUserOnline(wsMessage.getReceiverId());

        Map<String, Object> statusResponse = Map.of(
                "action", "ONLINE_STATUS",
                "userId", wsMessage.getReceiverId(),
                "isOnline", isOnline
        );

        sessionManager.sendMessageToUser(wsMessage.getSenderId(), statusResponse);
    }

    private void notifyContactsUserOnline(Long userId) {
        List<Long> contactIds = messagingService.getUserContacts(userId);

        Map<String, Object> onlineNotification = Map.of(
                "action", "USER_ONLINE",
                "userId", userId
        );

        contactIds.forEach(contactId -> {
            if (sessionManager.isUserOnline(contactId)) {
                sessionManager.sendMessageToUser(contactId, onlineNotification);
            }
        });
    }

    private void notifyContactsUserOffline(Long userId) {
        List<Long> contactIds = messagingService.getUserContacts(userId);

        Map<String, Object> offlineNotification = Map.of(
                "action", "USER_OFFLINE",
                "userId", userId
        );

        contactIds.forEach(contactId -> {
            if (sessionManager.isUserOnline(contactId)) {
                sessionManager.sendMessageToUser(contactId, offlineNotification);
            }
        });
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
