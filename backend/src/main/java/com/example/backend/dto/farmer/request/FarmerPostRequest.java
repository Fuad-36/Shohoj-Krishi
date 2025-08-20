package com.example.backend.dto.farmer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FarmerPostRequest {

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

