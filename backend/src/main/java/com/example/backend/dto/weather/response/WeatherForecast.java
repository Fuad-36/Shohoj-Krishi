package com.example.backend.dto.weather.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherForecast {
    private LocalDate date;
    private Double maxTemp;
    private Double minTemp;
    private String condition;
    private String conditionBangla;
    private String iconUrl;
    private Double humidity;
    private Double rainfall;
    private Double windSpeed;
    private Double uvIndex;
    private String farmingTips;
}
