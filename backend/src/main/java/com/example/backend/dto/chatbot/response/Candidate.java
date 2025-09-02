package com.example.backend.dto.chatbot.response;

import com.example.backend.dto.chatbot.request.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    private Content content;
    private String finishReason;
    private Integer index;
    private List<SafetyRating> safetyRatings;
}
