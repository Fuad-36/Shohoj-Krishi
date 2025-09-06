package com.example.backend.dto.news.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsSearchRequest {
    private String query;
    private String language = "bn";
    private int limit = 10;
    private String[] sources;
    private String category;
    private boolean includeTranslation = true;
    private String dateFrom;
    private String dateTo;
}
