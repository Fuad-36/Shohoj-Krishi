package com.example.backend.service.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import com.example.backend.dto.chatbot.request.PersonalizedChatRequest;
import com.example.backend.dto.chatbot.response.PersonalizedChatResponse;
import com.example.backend.entity.chatbot.ChatHistory;
import com.example.backend.repository.chatbot.ChatHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PersonalizedChatbotService {

    private final GeminiChatService geminiChatService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final ObjectMapper objectMapper;
    private final ChatIntentClassifier chatIntentClassifier;

    public PersonalizedChatResponse processMessage(PersonalizedChatRequest request) {
        log.info("Processing personalized Gemini chatbot message from user {}: {}", request.getUserId(), request.getMessage());

        PersonalizedChatResponse response = geminiChatService.generatePersonalizedResponse(request);
        saveChatHistory(request, response);

        return response;
    }

    private void saveChatHistory(PersonalizedChatRequest request, PersonalizedChatResponse response) {
        ChatType detectedType= chatIntentClassifier.detectChatType(request.getMessage());
        try {
            String contextJson = null;
            if (response.getFarmerContext() != null) {
                contextJson = objectMapper.writeValueAsString(response.getFarmerContext());
            }

            ChatHistory history = ChatHistory.builder()
                    .userId(request.getUserId())
                    .userMessage(request.getMessage())
                    .botResponse(response.getResponse())
                    .chatType(detectedType)
                    .language(request.getLanguage())
                    .wasPersonalized(response.getFarmerContext() != null)
                    .contextUsed(contextJson)
                    .build();

            chatHistoryRepository.save(history);
            log.debug("Saved personalized Gemini chat history for user {}", request.getUserId());

        } catch (Exception e) {
            log.error("Failed to save chat history: {}", e.getMessage());
        }
    }

    public List<ChatHistory> getChatHistory(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ChatHistory> historyPage = chatHistoryRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
        return historyPage.getContent();
    }

    public List<String> getChatSuggestions(Long userId, ChatType chatType) {
        // Get recent chat patterns to suggest relevant questions
        Pageable pageable = PageRequest.of(0, 5);
        List<ChatHistory> recentChats = chatHistoryRepository.findRecentChatByType(userId, chatType, pageable);

        return List.of(
                "আমার ফসলের বাজার দাম জানতে চাই",
                "আমার এলাকার আবহাওয়া অনুযায়ী পরামর্শ চাই",
                "আমার চাষাবাদের জন্য ঋণ পরামর্শ চাই",
                "আমার ফসলের রোগ-বালাই সমাধান চাই"
        );
    }

    // Additional method to get conversation context for multi-turn chats
    public PersonalizedChatResponse processConversationMessage(
            PersonalizedChatRequest request,
            List<ChatHistory> conversationHistory) {

        log.info("Processing conversation message with {} previous messages", conversationHistory.size());

        // You can enhance this to pass conversation history to Gemini for better context
        PersonalizedChatResponse response = geminiChatService.generatePersonalizedResponse(request);
        saveChatHistory(request, response);

        return response;
    }
}
