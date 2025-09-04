package com.example.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "weather")
@Data
public class WeatherConfig {

    // OpenWeatherMap free API - 1000 calls/day, 60 calls/minute
    private String openWeatherApiKey = ""; // Get free from https://openweathermap.org/api
    private String openWeatherBaseUrl = "https://api.openweathermap.org/data/2.5";

    // WeatherAPI free tier - 1 million calls/month
    private String weatherApiKey = ""; // Get free from https://www.weatherapi.com/
    private String weatherApiBaseUrl = "https://api.weatherapi.com/v1";

    // Alternative free weather sources
    private String weatherStackApiKey = ""; // Get free from https://weatherstack.com/ - 1000 calls/month
    private String weatherStackBaseUrl = "https://api.weatherstack.com";

    // Free weather data sources (no API key needed)
    private String openMeteoBaseUrl = "https://api.open-meteo.com/v1"; // Completely free
    private String wttrBaseUrl = "https://wttr.in"; // Free weather service

    private int forecastDays = 5; // Free tier limit
    private String defaultLanguage = "bn";
    private int cacheTimeoutMinutes = 15;

    @Bean
    public RestTemplate weatherRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
