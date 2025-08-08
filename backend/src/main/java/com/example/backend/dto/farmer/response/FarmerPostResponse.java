package com.example.backend.dto.farmer.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerPostResponse {
    private Long id;
    private String cropName;
    private  String cropType;
    private BigDecimal quantity;
    private String quantityUnit; // kg, ton, piece, etc.
    private BigDecimal pricePerUnit;
    private LocalDate harvestDate;
    private String status; // available, reserved, sold

    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;

    private String cropImageUrl;
    private Instant createdAt;
}

