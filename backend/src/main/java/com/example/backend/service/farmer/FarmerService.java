package com.example.backend.service.farmer;

import com.example.backend.dto.farmer.request.FarmerPostCreateRequest;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerPostResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;

import java.util.List;

public interface FarmerService {
    FarmerProfileResponse getCurrentFarmerProfile();
    FarmerProfileResponse updateFarmerProfile(ProfileUpdateRequest profileUpdateRequest);
    void createPost(FarmerPostCreateRequest request);
    List<FarmerPostResponse> getMyPosts();

}
