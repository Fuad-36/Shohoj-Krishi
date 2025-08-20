package com.example.backend.service.buyer;

import com.example.backend.dto.buyer.request.BuyerProfileUpdateRequest;
import com.example.backend.dto.buyer.resoponse.BuyerCropViewResponse;
import com.example.backend.dto.buyer.resoponse.BuyerProfileResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerService {
    BuyerProfileResponse getCurrentBuyerProfile();

    BuyerProfileResponse updateBuyerProfile(BuyerProfileUpdateRequest request);

    Page<BuyerCropViewResponse> getAvailableCrops(Pageable pageable,
                                                  String cropName,
                                                  String cropType,
                                                  String division,
                                                  String district,
                                                  String upazila);
}
