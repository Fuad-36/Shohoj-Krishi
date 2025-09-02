package com.example.backend.dto.chatbot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerationConfig {
    private Double temperature;
    private Integer maxOutputTokens;
    private Double topP;
    private Integer topK;
}
