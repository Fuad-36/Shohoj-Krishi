package com.example.backend.entity.auth;

import com.example.backend.entity.profile.AdminProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private AdminProfile approvedBy;

    @CreationTimestamp
    private Instant approvedAt;

    private String notes;
}
