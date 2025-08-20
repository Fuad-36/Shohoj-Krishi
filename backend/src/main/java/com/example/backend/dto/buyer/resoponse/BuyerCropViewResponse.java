package com.example.backend.dto.buyer.resoponse;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerCropViewResponse {

    private Long id;

    private String cropName;
    private String cropType;
    private String description;
    private String cropImageUrl;

    private BigDecimal quantity;
    private String quantityUnit;
    private BigDecimal pricePerUnit;

    private String division;
    private String district;

    private String farmerName;
    private String farmerPhone;
    private String farmerEmail;

    private Instant createdAt;
}

