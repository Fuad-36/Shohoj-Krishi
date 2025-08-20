package com.example.backend.dto.farmer.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerProfileResponse {
    private String fullName;
    private String nidNumber;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;
    private BigDecimal farmSizeAc;
    private String farmType;
    private String avatarUrl;

    private String phone;
    private String message;
}

