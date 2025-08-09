package com.example.backend.dto.buyer.resoponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyerProfileResponse {
    private String fullName;
    private String organisation;
    private String nidNumber;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;
    private String avatarUrl;

    private String phone;
    private String message;
}
