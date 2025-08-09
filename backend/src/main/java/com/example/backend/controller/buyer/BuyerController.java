package com.example.backend.controller.buyer;


import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.service.buyer.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
@RequiredArgsConstructor
public class BuyerController {
    private final BuyerService buyerService;

    // Endpoint to get the current buyer's profile
    @GetMapping("/me")
    public BuyerProfileResponse getMyProfile() {
        return buyerService.getCurrentBuyerProfile();
    }
    // Endpoint to update the buyer's profile
    @PutMapping("/profile")
    public BuyerProfileResponse updateProfile(@RequestBody BuyerProfileUpdateRequest request) {
        return buyerService.updateBuyerProfile(request);
    }
}
