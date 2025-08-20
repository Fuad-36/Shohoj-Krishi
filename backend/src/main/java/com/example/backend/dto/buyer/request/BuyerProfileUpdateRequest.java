package com.example.backend.dto.buyer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyerProfileUpdateRequest {
    private String fullName;
    private String nidNumber;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;
    private String organisation;
    private String avatarUrl;
    private String phone;
}
