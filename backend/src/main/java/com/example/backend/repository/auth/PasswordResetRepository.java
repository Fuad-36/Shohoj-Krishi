package com.example.backend.repository.auth;

import com.example.backend.entity.auth.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(UUID token);
}
