package com.example.backend.repository.profile;

import com.example.backend.entity.profile.AuthorityProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityProfileRepository extends JpaRepository<AuthorityProfile, Long> {
}
