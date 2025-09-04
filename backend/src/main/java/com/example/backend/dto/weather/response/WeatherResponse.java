package com.example.backend.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherResponse {
    private CurrentWeather currentWeather;
    private List<WeatherForecast> forecast;
    private List<WeatherAlert> alerts;
    private FarmingWeatherAdvice farmingAdvice;
    private String location;
    private String language;
    private LocalDateTime lastUpdated;
    private String dataSource;
    private Map<String, Object> metadata;
}
