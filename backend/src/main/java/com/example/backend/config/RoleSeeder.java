package com.example.backend.config;

import com.example.backend.entity.auth.Role;
import com.example.backend.entity.auth.RoleCode;
import com.example.backend.repository.auth.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        seedRole("FARMER", "Farmer", "Default role for farmers");
        seedRole("BUYER", "Buyer", "Default role for buyers");
        seedRole("AUTHORITY", "Authority", "Govt agriculture authority");
        seedRole("ADMIN", "Admin", "Platform admin");
    }

    private void seedRole(String code, String name, String description) {
        if (!roleRepository.existsByCode(RoleCode.valueOf(code))) {
            roleRepository.save(
                    Role.builder()
                            .code(RoleCode.valueOf(code))
                            .name(name)
                            .description(description)
                            .build()
            );
        }
    }
}
