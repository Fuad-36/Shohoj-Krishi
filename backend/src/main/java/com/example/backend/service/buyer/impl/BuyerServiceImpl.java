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
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public Page<BuyerCropViewResponse> getAvailableCrops(
            Pageable pageable,
            String cropName,
            String cropType,
            String division,
            String district,
            String upazila
    ) {
        return farmerPostRepository.findAll((root, query, cb) -> {
            // Join User -> FarmerProfile to avoid N+1 problem
            root.fetch("user", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), PostStatus.AVAILABLE));

            if (cropName != null && !cropName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("cropName")), "%" + cropName.toLowerCase() + "%"));
            }

            if (cropType != null && !cropType.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("cropType")), cropType.toLowerCase()));
            }

            if (division != null && !division.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("division")), division.toLowerCase()));
            }

            if (district != null && !district.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.toLowerCase()));
            }

            if (upazila != null && !upazila.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("upazila")), upazila.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(this::mapToBuyerResponse);
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
