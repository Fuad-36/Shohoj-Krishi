package com.example.backend.dto.chatbot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalizedChatRequest {
    private Long userId;
    private String message;
    private String language; // "bn" for Bengali, "en" for English
    private boolean includePersonalContext = true; // Whether to use farmer's data
}
