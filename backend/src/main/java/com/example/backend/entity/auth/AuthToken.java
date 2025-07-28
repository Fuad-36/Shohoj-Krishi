package com.example.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true)
    private UUID refreshToken;

    @Column(columnDefinition = "inet")
    private String ipAddress;

    private String userAgent;

    private Instant expiresAt;

    @CreationTimestamp
    private Instant createdAt;

    private Boolean revoked = false;
}
