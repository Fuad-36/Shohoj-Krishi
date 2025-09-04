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
public class CropSpecificAdvice {
    private String cropName;
    private String cropNameBangla;
    private String advice;
    private String riskLevel;
    private List<String> actions;
}
