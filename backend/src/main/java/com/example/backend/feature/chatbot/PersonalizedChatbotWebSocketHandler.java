package com.example.backend.feature.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import com.example.backend.dto.chatbot.request.PersonalizedChatRequest;
import com.example.backend.service.chatbot.ChatIntentClassifier;
import com.example.backend.service.chatbot.PersonalizedChatbotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalizedChatbotWebSocketHandler {

    private final PersonalizedChatbotService chatbotService;
    private final ObjectMapper objectMapper;
    private final ChatIntentClassifier chatIntentClassifier;

    public void handlePersonalizedChatbotMessage(Long userId, String message, ChatType chatType, WebSocketSession session) {
        ChatType detectedChatType = chatIntentClassifier.detectChatType(message);
        try {
            PersonalizedChatRequest request = PersonalizedChatRequest.builder()
                    .userId(userId)
                    .message(message)
                    .language("bn")
                    .includePersonalContext(true)
                    .build();

            // Process in separate thread to avoid blocking WebSocket
            CompletableFuture.supplyAsync(() -> chatbotService.processMessage(request))
                    .thenAccept(response -> {
                        try {
                            Map<String, Object> wsResponse = Map.of(
                                    "action", "GEMINI_CHATBOT_RESPONSE",
                                    "response", response
                            );

                            String responseJson = objectMapper.writeValueAsString(wsResponse);
                            session.sendMessage(new TextMessage(responseJson));

                            log.debug("Sent Gemini chatbot response to user {}", userId);

                        } catch (Exception e) {
                            log.error("Failed to send Gemini chatbot response via WebSocket: {}", e.getMessage());
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Error processing chatbot message: {}", throwable.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error processing Gemini chatbot message: {}", e.getMessage());
        }
    }
}
