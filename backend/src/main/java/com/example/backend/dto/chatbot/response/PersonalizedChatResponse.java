package com.example.backend.dto.chatbot.response;

import com.example.backend.dto.chatbot.request.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalizedChatResponse {
    private String response;
    private String originalLanguage;
    private ChatType chatType;
    private List<String> relatedSuggestions;
    private FarmerContext farmerContext; // What data was used for personalization
    private Map<String, Object> metadata;
}
