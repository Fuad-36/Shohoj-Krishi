package com.example.backend.repository.auth;

import com.example.backend.entity.auth.OtpCode;
import com.example.backend.entity.auth.OtpCode.TargetType;
import com.example.backend.entity.auth.OtpCode.Purpose;
import com.example.backend.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByUserAndTargetTypeAndTargetValueAndPurposeAndUsedFalseAndExpiresAtAfter(
            User user,
            TargetType targetType,
            String targetValue,
            Purpose purpose,
            Instant now
    );
}
