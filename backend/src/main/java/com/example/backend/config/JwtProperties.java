package com.example.backend.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long expiration;

    @Value("${app.jwt.refresh.expiration}")
    private long refreshExpiration;

    @Value("${app.jwt.issuer}")
    private String issuer;
}