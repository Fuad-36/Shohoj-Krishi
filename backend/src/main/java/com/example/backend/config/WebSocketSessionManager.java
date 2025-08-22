package com.example.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebSocketSessionManager {

    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        userSessions.put(userId, session);
        log.info("User {} connected", userId);
    }

    public void removeSession(Long userId) {
        userSessions.remove(userId);
        log.info("User {} disconnected", userId);
    }

    public WebSocketSession getSession(Long userId) {
        return userSessions.get(userId);
    }

    public boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    public Set<Long> getOnlineUsers() {
        return userSessions.entrySet().stream()
                .filter(entry -> entry.getValue().isOpen())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void sendMessageToUser(Long userId, Object message) {
        WebSocketSession session = getSession(userId);
        if (session != null && session.isOpen()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String messageJson = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
            } catch (IOException e) {
                log.error("Failed to send message to user {}: {}", userId, e.getMessage());
                removeSession(userId);
            }
        }
    }
}