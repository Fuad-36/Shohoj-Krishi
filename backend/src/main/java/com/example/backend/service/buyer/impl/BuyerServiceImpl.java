package com.example.backend.service.buyer.impl;

import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerCropViewResponse;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.posts.FarmerPost;
import com.example.backend.entity.posts.PostStatus;
import com.example.backend.entity.profile.BuyerProfile;
import com.example.backend.entity.profile.FarmerProfile;
import com.example.backend.repository.farmer.FarmerPostRepository;
import com.example.backend.repository.profile.BuyerProfileRepository;
import com.example.backend.repository.profile.FarmerProfileRepository;
import com.example.backend.service.buyer.BuyerService;
import com.example.backend.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {

    private final CurrentUserUtil currentUserUtil;
    private final BuyerProfileRepository buyerProfileRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final FarmerPostRepository farmerPostRepository;
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

    @Override
    public Page<BuyerCropViewResponse> getAvailableCrops(Pageable pageable) {
        return farmerPostRepository
                .findByStatus(PostStatus.AVAILABLE, pageable)
                .map(this::mapToBuyerResponse);
    }

    private BuyerCropViewResponse mapToBuyerResponse(FarmerPost post) {
        FarmerProfile farmerProfile = farmerProfileRepository
                .findById(post.getUser().getId())
                .orElse(null);

        return BuyerCropViewResponse.builder()
                .id(post.getId())
                .cropName(post.getCropName())
                .cropType(post.getCropType())
                .description(post.getDescription())
                .cropImageUrl(post.getCropImageUrl())
                .quantity(post.getQuantity())
                .quantityUnit(post.getQuantityUnit())
                .pricePerUnit(post.getPricePerUnit())
                .division(post.getDivision())
                .district(post.getDistrict())
                .farmerName(farmerProfile != null ? farmerProfile.getFullName() : null)
                .farmerPhone(post.getUser().getPhone())
                .farmerEmail(post.getUser().getEmail())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
