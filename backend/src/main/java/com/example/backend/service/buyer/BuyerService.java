package com.example.backend.service.buyer;

import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;

public interface BuyerService {
    BuyerProfileResponse getCurrentBuyerProfile();

    BuyerProfileResponse updateBuyerProfile(BuyerProfileUpdateRequest request);
}
