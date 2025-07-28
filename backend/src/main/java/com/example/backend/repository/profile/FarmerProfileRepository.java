package com.example.backend.repository.profile;

import com.example.backend.entity.profile.FarmerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerProfileRepository extends JpaRepository<FarmerProfile, Long> {
}
