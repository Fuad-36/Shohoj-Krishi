package com.example.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "chatbot")
@Data
public class ChatbotConfig {

    private String geminiApiKey = ""; // Get free API key from https://makersuite.google.com/app/apikey
    private String geminiModel = "gemini-2.5-flash"; // Free tier model - NO COST
    private String geminiBaseUrl = "https://generativelanguage.googleapis.com/v1beta/models";
    private double temperature = 0.7;
    private int maxTokens = 4092; // Gemini free tier supports up to 2048 tokens
    private int maxContextLength = 10000; // Characters for context
    private int safetyLevel = 2; // 0-4, higher is more restrictive

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
