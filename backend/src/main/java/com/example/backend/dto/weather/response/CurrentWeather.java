package com.example.backend.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentWeather {
    private Double temperature;
    private Double feelsLike;
    private Double humidity;
    private Double windSpeed;
    private String windDirection;
    private Double pressure;
    private Double visibility;
    private String condition;
    private String conditionBangla;
    private String iconUrl;
    private Double uvIndex;
    private Double rainfall;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
}
