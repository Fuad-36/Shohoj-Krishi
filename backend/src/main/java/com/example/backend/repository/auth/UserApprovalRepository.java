package com.example.backend.repository.auth;

import com.example.backend.entity.auth.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserApprovalRepository extends JpaRepository<UserApproval, Long> {
    Optional<UserApproval> findByUserId(Long userId);
}
