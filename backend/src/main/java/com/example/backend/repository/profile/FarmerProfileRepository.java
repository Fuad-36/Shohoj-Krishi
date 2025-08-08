package com.example.backend.repository.profile;

import com.example.backend.entity.auth.User;
import com.example.backend.entity.profile.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
    Optional<Object> findByUser(User user);
}
