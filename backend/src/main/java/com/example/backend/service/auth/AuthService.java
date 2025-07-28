package com.example.backend.service.auth;

import com.example.backend.dto.auth.request.RegisterRequest;
import com.example.backend.dto.auth.request.VerifyOtpRequest;
import com.example.backend.dto.auth.response.RegisterResponse;
import com.example.backend.dto.auth.response.MessageResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    MessageResponse verifyOtp(VerifyOtpRequest request);
    MessageResponse resendOtp(String email);
}