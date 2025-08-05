package com.example.backend.controller.farmer;

import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.service.farmer.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    // Endpoint to get the current farmer's profile
    @GetMapping("/me")
    public FarmerProfileResponse getMyProfile() {
        return farmerService.getCurrentFarmerProfile();
    }
    // Endpoint to update the farmer's profile
    @PutMapping("/profile")
    public FarmerProfileResponse updateProfile(@RequestBody ProfileUpdateRequest profileUpdateRequest) {
        return farmerService.updateFarmerProfile(profileUpdateRequest);
    }
}
