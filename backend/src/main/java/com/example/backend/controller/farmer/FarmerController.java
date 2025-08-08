package com.example.backend.controller.farmer;

import com.example.backend.dto.farmer.request.FarmerPostCreateRequest;
import com.example.backend.dto.farmer.request.ProfileUpdateRequest;
import com.example.backend.dto.farmer.response.FarmerPostResponse;
import com.example.backend.dto.farmer.response.FarmerProfileResponse;
import com.example.backend.service.farmer.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    // Endpoint to create a new post
    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(
            @RequestBody FarmerPostCreateRequest request
    ) {
        farmerService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // returns 201 Created
    }
    // Endpoint to get all posts created by the current farmer
    @GetMapping("/posts")
    public ResponseEntity<List<FarmerPostResponse>> getMyPosts() {
        List<FarmerPostResponse> posts = farmerService.getMyPosts();
        return ResponseEntity.ok(posts);
    }
}
