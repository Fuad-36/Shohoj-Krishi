package com.example.backend.config;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.RoleCode;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.auth.UserStatus;
import com.example.backend.entity.profile.AdminProfile;
import com.example.backend.repository.auth.RoleRepository;
import com.example.backend.repository.auth.UserRepository;
import com.example.backend.repository.profile.AdminProfileRepository;
import com.example.backend.service.AdminInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class AdminDataInitializer {

    private final AdminInitializationService adminInitializationService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner createInitialAdmin() {
        return args -> adminInitializationService.initializeAdmin(adminEmail, adminPassword);
    }
}