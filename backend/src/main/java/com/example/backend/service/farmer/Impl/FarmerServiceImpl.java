package com.example.backend.service.farmer.Impl;

import com.example.backend.dto.farmer.request.FarmerPostCreateRequest;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerPostResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.entity.auth.User;
import com.example.backend.entity.posts.FarmerPost;
import com.example.backend.entity.posts.PostStatus;
import com.example.backend.entity.profile.FarmerProfile;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.farmer.FarmerPostRepository;
import com.example.backend.repository.profile.FarmerProfileRepository;
import com.example.backend.service.farmer.FarmerService;
import com.example.backend.util.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final CurrentUserUtil currentUserUtil;
    private final FarmerProfileRepository farmerProfileRepository;
    private final FarmerPostRepository farmerPostRepository;

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

    @Override
    public FarmerProfileResponse updateFarmerProfile(ProfileUpdateRequest request) {

        User user = currentUserUtil.getCurrentUser();
        Long userId = user.getId();
        FarmerProfile profile = farmerProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(request.getFullName());
        profile.setNidNumber(request.getNidNumber());
        profile.setDivision(request.getDivision());
        profile.setDistrict(request.getDistrict());
        profile.setUpazila(request.getUpazila());
        profile.setUnionPorishod(request.getUnionPorishod());
        profile.setAddress(request.getAddress());
        profile.setFarmSizeAc(request.getFarmSizeAc());
        profile.setFarmType(request.getFarmType());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.getUser().setPhone(request.getPhone());

        FarmerProfile updated = farmerProfileRepository.save(profile);

        return FarmerProfileResponse.builder()
                .fullName(updated.getFullName())
                .nidNumber(updated.getNidNumber())
                .division(updated.getDivision())
                .district(updated.getDistrict())
                .upazila(updated.getUpazila())
                .unionPorishod(updated.getUnionPorishod())
                .address(updated.getAddress())
                .farmSizeAc(updated.getFarmSizeAc())
                .farmType(updated.getFarmType())
                .avatarUrl(updated.getAvatarUrl())
                .phone(updated.getUser().getPhone())
                .message("Profile updated successfully")
                .build();
    }

    @Override
    public void createPost(FarmerPostCreateRequest request) {
        User user = currentUserUtil.getCurrentUser(); // helper that gets authenticated user
        FarmerProfile profile = (FarmerProfile) farmerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Farmer profile not found"));

        FarmerPost post = FarmerPost.builder()
                .user(user)
                .title(request.getTitle())
                .cropType(request.getCropType())
                .cropImageUrl(request.getCropImageUrl())
                .cropName(request.getCropName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .quantityUnit(request.getQuantityUnit())
                .pricePerUnit(request.getPricePerUnit())
                .harvestDate(request.getHarvestDate() != null ? request.getHarvestDate() : LocalDate.now())

                // location from profile
                .division(profile.getDivision())
                .district(profile.getDistrict())
                .upazila(profile.getUpazila())
                .unionPorishod(profile.getUnionPorishod())

                .status(PostStatus.valueOf("AVAILABLE"))
                .build();

        farmerPostRepository.save(post);

    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmerPostResponse> getMyPosts() {
        List<FarmerPost> posts = farmerPostRepository.findAllByUser_Id(currentUserUtil.getCurrentUserId());
        if (posts.isEmpty()) {
            throw new ResourceNotFoundException("No posts found for the current user");
        }
        return posts.stream().map(post -> FarmerPostResponse.builder()
                .id(post.getId())
                .cropName(post.getCropName())
                .cropType(post.getCropType())
                .cropImageUrl(post.getCropImageUrl())
                .quantity(post.getQuantity())
                .quantityUnit(post.getQuantityUnit())
                .pricePerUnit(post.getPricePerUnit())
                .harvestDate(post.getHarvestDate())
                .status(post.getStatus().name())
                .division(post.getDivision())
                .district(post.getDistrict())
                .upazila(post.getUpazila())
                .unionPorishod(post.getUnionPorishod())
                .cropImageUrl(post.getCropImageUrl())
                .createdAt(post.getCreatedAt())
                .build()
        ).toList();
    }


}
