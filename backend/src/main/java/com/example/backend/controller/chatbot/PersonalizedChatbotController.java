package com.example.backend.controller.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import com.example.backend.dto.chatbot.request.PersonalizedChatRequest;
import com.example.backend.dto.chatbot.response.PersonalizedChatResponse;
import com.example.backend.entity.chatbot.ChatHistory;
import com.example.backend.service.chatbot.PersonalizedChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
public class PersonalizedChatbotController {

    private final PersonalizedChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<PersonalizedChatResponse> chat(@RequestBody PersonalizedChatRequest request) {
        try {
            PersonalizedChatResponse response = chatbotService.processMessage(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing chat request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(request));
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatHistory>> getChatHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {

        List<ChatHistory> history = chatbotService.getChatHistory(userId, limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/suggestions/{userId}")
    public ResponseEntity<List<String>> getChatSuggestions(
            @PathVariable Long userId,
            @RequestParam(required = false) ChatType chatType) {

        List<String> suggestions = chatbotService.getChatSuggestions(userId,
                chatType != null ? chatType : ChatType.GENERAL_FARMING);
        return ResponseEntity.ok(suggestions);
    }

    private PersonalizedChatResponse createErrorResponse(PersonalizedChatRequest request) {
        String errorMessage = "bn".equals(request.getLanguage()) ?
                "দুঃখিত, আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না।" :
                "Sorry, I cannot answer your question right now.";

        return PersonalizedChatResponse.builder()
                .response(errorMessage)
                .originalLanguage(request.getLanguage())
                .relatedSuggestions(List.of())
                .metadata(Map.of("error", true))
                .build();
    }
}
