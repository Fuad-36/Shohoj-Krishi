package com.example.backend.dto.farmer.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FarmerPostCreateRequest {

    private String title;
    private String description;
    private String cropName;
    private String cropType;
    private String cropImageUrl;

    private BigDecimal quantity;
    private String quantityUnit;

    private BigDecimal pricePerUnit;

    private LocalDate harvestDate;
}

