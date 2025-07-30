package com.example.backend.service.impl;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.RoleCode;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.auth.UserStatus;
import com.example.backend.entity.profile.AdminProfile;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.auth.RoleRepository;
import com.example.backend.repository.auth.UserRepository;
import com.example.backend.repository.profile.AdminProfileRepository;
import com.example.backend.service.AdminInitializationService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminInitializationServiceImpl implements AdminInitializationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void initializeAdmin(String adminEmail, String adminPassword) {
        Optional<User> existing = userRepository.findByEmail(adminEmail);
        if (existing.isPresent()) {
            System.out.println("Admin user already exists, skipping initialization");
            return;
        }

        Role adminRole = roleRepository.findByCode(RoleCode.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role code ADMIN not found"));

        User adminUser = User.builder()
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .emailVerified(true)
                .phoneVerified(true)
                .isActive(true)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(adminUser);
        entityManager.flush();
        entityManager.clear();

        User managedUser = userRepository.findById(adminUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after save"));

        AdminProfile profile = AdminProfile.builder()
                .user(managedUser)
                .fullName("Super Admin")
                .notes("Initial system administrator")
                .build();

        adminProfileRepository.save(profile);

        System.out.println("Initial admin user & profile created with ID: " + managedUser.getId());
    }
}
