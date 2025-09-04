package com.example.backend.controller.weather;

import com.example.backend.dto.weather.request.WeatherRequest;
import com.example.backend.dto.weather.request.WeatherType;
import com.example.backend.dto.weather.response.WeatherResponse;
import com.example.backend.service.weather.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/current")
    public ResponseEntity<WeatherResponse> getCurrentWeather(@RequestBody WeatherRequest request) {
        try {
            request.setWeatherType(WeatherType.CURRENT);
            WeatherResponse response = weatherService.getCurrentWeather(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getCurrentWeather: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/forecast")
    public ResponseEntity<WeatherResponse> getWeatherForecast(@RequestBody WeatherRequest request) {
        try {
            request.setWeatherType(WeatherType.FORECAST);
            WeatherResponse response = weatherService.getWeatherForecast(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getWeatherForecast: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/comprehensive")
    public ResponseEntity<WeatherResponse> getComprehensiveWeather(@RequestBody WeatherRequest request) {
        try {
            request.setWeatherType(WeatherType.COMPREHENSIVE);
            WeatherResponse response = weatherService.getComprehensiveWeather(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getComprehensiveWeather: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/current/{location}")
    public ResponseEntity<WeatherResponse> getCurrentWeatherByLocation(@PathVariable String location) {
        try {
            WeatherRequest request = WeatherRequest.builder()
                    .location(location)
                    .language("bn")
                    .weatherType(WeatherType.CURRENT)
                    .includeFarmingAdvice(false)
                    .build();

            WeatherResponse response = weatherService.getCurrentWeather(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getCurrentWeatherByLocation: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/forecast/{location}")
    public ResponseEntity<WeatherResponse> getForecastByLocation(@PathVariable String location) {
        try {
            WeatherRequest request = WeatherRequest.builder()
                    .location(location)
                    .language("bn")
                    .weatherType(WeatherType.FORECAST)
                    .includeFarmingAdvice(false)
                    .build();

            WeatherResponse response = weatherService.getWeatherForecast(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getForecastByLocation: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
