package com.example.backend.entity.profile;

import com.example.backend.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "farmer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    private String nidNumber;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;

    @Column(precision = 10, scale = 2)
    private BigDecimal farmSizeAc;

    private String farmType; // dairy, poultry, crop etc
    private String avatarUrl;


}
