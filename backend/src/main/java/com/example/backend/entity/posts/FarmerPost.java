package com.example.backend.entity.posts;

import com.example.backend.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "farmer_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String cropName; // e.g. rice, wheat, maize
    private String cropImageUrl;
    private String cropType;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantity;

    private String quantityUnit; // ton, kg, piece, etc

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    private LocalDate harvestDate;

    // These will be populated from FarmerProfile during post creation
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;

    @Enumerated(EnumType.STRING)
    private PostStatus status; // available, reserved, sold

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}


