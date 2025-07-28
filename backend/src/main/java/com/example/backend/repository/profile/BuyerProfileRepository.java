package com.example.backend.repository.profile;

import com.example.backend.entity.profile.BuyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerProfileRepository extends JpaRepository<BuyerProfile, Long> {
}
