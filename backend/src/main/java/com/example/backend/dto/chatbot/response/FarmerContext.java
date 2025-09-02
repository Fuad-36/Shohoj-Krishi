package com.example.backend.dto.chatbot.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmerContext {
    private String farmerName;
    private String location; // "Upazila, District, Division"
    private BigDecimal farmSize;
    private String farmType;
    private List<String> currentCrops; // From active posts
    private List<PostInfo> recentPosts;
    private String expertise; // Based on post history
}
