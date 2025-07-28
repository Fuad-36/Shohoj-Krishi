package com.example.backend.dto.auth.request;

import com.example.backend.entity.auth.RoleCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Role is required")
    private RoleCode role; // FARMER, BUYER, or AUTHORITY

    // Common fields
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String division;
    private String district;
    private String upazila;
    private String union;
    private String address;

    // For farmers and buyers
    private String nidNumber;

    // For farmers only
    private BigDecimal farmSizeAc;
    private String farmType;

    // For buyers only
    private String organisation;

    // For authority only
    private String designation;
    private String employeeId;
    private String employeeIdImageUrl; // URL of uploaded ID card image
    private String officeDivision;
    private String officeDistrict;
    private String officeUpazila;
    private String officeUnion;
}