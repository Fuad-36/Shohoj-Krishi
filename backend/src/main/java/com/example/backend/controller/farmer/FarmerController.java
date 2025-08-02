package com.example.backend.controller.farmer;

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

    @GetMapping("/me")
    public FarmerProfileResponse getMyProfile() {
        return farmerService.getCurrentFarmerProfile();
    }
}
