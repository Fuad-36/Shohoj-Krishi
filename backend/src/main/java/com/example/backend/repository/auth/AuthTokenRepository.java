package com.example.backend.repository.auth;

import com.example.backend.entity.auth.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByRefreshToken(UUID refreshToken);
}
