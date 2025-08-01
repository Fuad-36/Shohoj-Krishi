package com.example.backend.service.auth;

import com.example.backend.dto.auth.request.LoginRequest;
import com.example.backend.dto.auth.request.RefreshTokenRequest;
import com.example.backend.dto.auth.request.RegisterRequest;
import com.example.backend.dto.auth.request.VerifyOtpRequest;
import com.example.backend.dto.auth.response.AuthResponse;
import com.example.backend.dto.auth.response.MessageResponse;
import com.example.backend.dto.auth.response.RegisterResponse;
import com.example.backend.entity.auth.User;

public interface AuthService {
    AuthResponse authenticate(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    RegisterResponse register(RegisterRequest request);
    MessageResponse verifyOtp(VerifyOtpRequest request);
    MessageResponse resendOtp(String email);

}
