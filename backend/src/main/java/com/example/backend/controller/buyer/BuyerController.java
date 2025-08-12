package com.example.backend.controller.buyer;


import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerCropViewResponse;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.service.buyer.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // Endpoint to view all crops available for buyers

    @GetMapping("/crops")
    public Page<BuyerCropViewResponse> getAvailableCrops(
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String division,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String upazila,
            Pageable pageable
    ) {
        return buyerService.getAvailableCrops(pageable,cropName, cropType, division, district, upazila);
    }

    //    Supports pagination & sorting out of the box via Spring’s Pageable.
    //    URL example to fetch first 10 crops, newest first:
    //            /api/buyer/crops?page=0&size=10&sort=createdAt,desc
}
