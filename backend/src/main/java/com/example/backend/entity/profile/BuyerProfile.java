package com.example.backend.entity.profile;

import com.example.backend.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buyer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String organisation;
    private String fullName;
    private String nidNumber;
    private String division;
    private String district;
    private String upazila;
    private String unionPorishod;
    private String address;
    private String avatarUrl;
}
