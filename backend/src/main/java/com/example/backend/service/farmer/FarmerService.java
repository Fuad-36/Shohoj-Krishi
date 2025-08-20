package com.example.backend.service.farmer;

import com.example.backend.dto.buyer.resoponse.FarmerPostDetailsResponse;
import com.example.backend.dto.farmer.request.FarmerPostRequest;
import com.example.backend.dto.farmer.request.FarmerPostUpdateRequest;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerPostResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;

import java.util.List;

public interface FarmerService {
    FarmerProfileResponse getCurrentFarmerProfile();
    FarmerProfileResponse updateFarmerProfile(ProfileUpdateRequest profileUpdateRequest);
    void createPost(FarmerPostRequest request);
    List<FarmerPostResponse> getMyPosts();
    void updatePost(Long postId, FarmerPostUpdateRequest request);
    void deletePost(Long postId);

    FarmerPostDetailsResponse getCropDetailsById(Long id);
}
