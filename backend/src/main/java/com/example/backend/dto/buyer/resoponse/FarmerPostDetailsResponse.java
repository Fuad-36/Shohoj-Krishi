package com.example.backend.dto.buyer.resoponse;

import com.example.backend.entity.posts.PostStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerPostDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private String cropName;
    private String cropType;
    private String category;
    private BigDecimal pricePerUnit;
    private BigDecimal quantity;
    private String quantityUnit; // ton, kg, piece, etc
    private String cropImageUrl;
    private LocalDate harvestDate;
    private PostStatus status;

    // Farmer info
    private String farmerName;
    private String farmerPhone;
    private String farmerEmail;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;
    private Instant createdAt;
}

