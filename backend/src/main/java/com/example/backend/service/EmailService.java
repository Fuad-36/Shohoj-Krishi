package com.example.backend.service;

public interface EmailService {
    void sendOtpEmail(String to, String otp);
    void sendPasswordEmail(String to, String password, String fullName);
    void sendRejectionEmail(String to, String fullName, String reason);
    void sendAcceptationEmail(String to, String fullName);
}