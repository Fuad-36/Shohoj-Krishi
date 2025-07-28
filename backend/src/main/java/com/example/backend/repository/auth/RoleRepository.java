package com.example.backend.repository.auth;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
    boolean existsByCode(RoleCode code);
}
