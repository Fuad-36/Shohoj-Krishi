package com.example.backend.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FarmingWeatherAdvice {
    private String generalAdvice;
    private List<CropSpecificAdvice> cropAdvice;
    private List<String> immediateActions;
    private List<String> weeklyPlan;
    private String irrigationAdvice;
    private String pestDiseaseRisk;
    private String harvestRecommendation;
}
