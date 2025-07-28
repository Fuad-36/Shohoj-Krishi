package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class AppConfig {

    @Value("${app.otp.expiration:300}") // 5 minutes default
    private int otpExpirationSeconds;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.password.reset.expiration:3600}") // 1 hour default
    private int passwordResetExpirationSeconds;

    @Value("${app.auth.token.expiration:2592000}") // 30 days default
    private int authTokenExpirationSeconds;
}