package com.example.backend.service.buyer.impl;

import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.profile.BuyerProfile;
import com.example.backend.entity.profile.FarmerProfile;
import com.example.backend.repository.profile.BuyerProfileRepository;
import com.example.backend.service.buyer.BuyerService;
import com.example.backend.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

    private final CurrentUserUtil currentUserUtil;
    private final BuyerProfileRepository buyerProfileRepository;
    @Override
    public BuyerProfileResponse getCurrentBuyerProfile() {
        User user = currentUserUtil.getCurrentUser();
        Long userId = user.getId();
        BuyerProfile profile = buyerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));
        return BuyerProfileResponse.builder()
                .fullName(profile.getFullName())
                .organisation(profile.getOrganisation())
                .nidNumber(profile.getNidNumber())
                .division(profile.getDivision())
                .district(profile.getDistrict())
                .upazila(profile.getUpazila())
                .unionPorishod(profile.getUnionPorishod())
                .address(profile.getAddress())
                .avatarUrl(profile.getAvatarUrl())
                .phone(user.getPhone())
                .message("Profile retrieved successfully")
                .build();
    }

    @Override
    public BuyerProfileResponse updateBuyerProfile(BuyerProfileUpdateRequest request) {
        User user = currentUserUtil.getCurrentUser();
        Long userId = user.getId();
        BuyerProfile profile = buyerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setNidNumber(request.getNidNumber());
        profile.setDivision(request.getDivision());
        profile.setDistrict(request.getDistrict());
        profile.setUpazila(request.getUpazila());
        profile.setUnionPorishod(request.getUnionPorishod());
        profile.setAddress(request.getAddress());
        profile.setOrganisation(request.getOrganisation());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.getUser().setPhone(request.getPhone());

        BuyerProfile updated = buyerProfileRepository.save(profile);

        return BuyerProfileResponse.builder()
                .fullName(updated.getFullName())
                .nidNumber(updated.getNidNumber())
                .division(updated.getDivision())
                .district(updated.getDistrict())
                .upazila(updated.getUpazila())
                .unionPorishod(updated.getUnionPorishod())
                .address(updated.getAddress())
                .organisation(updated.getOrganisation())
                .avatarUrl(updated.getAvatarUrl())
                .phone(updated.getUser().getPhone())
                .message("Profile updated successfully")
                .build();
    }
}
