package com.example.backend.service.farmer.Impl;

import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.profile.FarmerProfile;
import com.example.backend.repository.profile.FarmerProfileRepository;
import com.example.backend.service.farmer.FarmerService;
import com.example.backend.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final CurrentUserUtil currentUserUtil;
    private final FarmerProfileRepository farmerProfileRepository;

    @Override
    public FarmerProfileResponse getCurrentFarmerProfile() {
        User user = currentUserUtil.getCurrentUser();
        Long userId = user.getId();
        FarmerProfile profile = farmerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));
        return FarmerProfileResponse.builder()
                .fullName(profile.getFullName())
                .nidNumber(profile.getNidNumber())
                .division(profile.getDivision())
                .district(profile.getDistrict())
                .upazila(profile.getUpazila())
                .unionPorishod(profile.getUnionPorishod())
                .address(profile.getAddress())
                .farmSizeAc(profile.getFarmSizeAc())
                .farmType(profile.getFarmType())
                .avatarUrl(profile.getAvatarUrl())
                .phone(user.getPhone())
                .message("Profile retrieved successfully")
                .build();
    }
}
