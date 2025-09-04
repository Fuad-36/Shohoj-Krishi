package com.example.backend.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherAlert {
    private String alertType;
    private String alertTypeBangla;
    private String severity;
    private String title;
    private String titleBangla;
    private String description;
    private String descriptionBangla;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> affectedAreas;
    private String farmingRecommendation;
}
