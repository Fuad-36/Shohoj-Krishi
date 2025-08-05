package com.example.backend.service.farmer;

import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;

public interface FarmerService {
    FarmerProfileResponse getCurrentFarmerProfile();
    FarmerProfileResponse updateFarmerProfile(ProfileUpdateRequest profileUpdateRequest);
}
