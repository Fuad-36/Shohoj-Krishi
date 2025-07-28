package com.example.backend.entity.profile;

import com.example.backend.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authority_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    private String designation;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String employeeIdImageUrl;
    private String officeDivision;
    private String officeDistrict;
    private String officeUpazila;
    private String officeUnion;
    private String address;
}
