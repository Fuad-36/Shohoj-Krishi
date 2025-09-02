package com.example.backend.dto.chatbot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInfo {
    private String cropName;
    private BigDecimal quantity;
    private String quantityUnit;
    private BigDecimal pricePerUnit;
    private LocalDate harvestDate;
    private String status;
}
