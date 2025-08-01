package com.example.backend.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Long userId;
    private String email;
    private String message;
    private boolean otpSent;
}
