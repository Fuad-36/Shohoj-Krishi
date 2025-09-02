package com.example.backend.dto.chatbot.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SafetySetting {
    private String category;
    private String threshold;
}
