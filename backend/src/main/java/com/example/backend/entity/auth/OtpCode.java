package com.example.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "otp_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {

    public enum TargetType {
        PHONE, EMAIL
    }

    public enum Purpose {
        SIGNUP, LOGIN, PASSWORD_RESET, TWO_FA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_value", nullable = false)
    private String targetValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose = Purpose.SIGNUP;

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Boolean used = false;

    private Short attempts = 0;

    @CreationTimestamp
    private Instant createdAt;
}
