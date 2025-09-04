package com.example.backend.dto.weather.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherRequest {
    private String location;
    private String language;
    private WeatherType weatherType;
    private Long userId;
    private boolean includeFarmingAdvice;
}
