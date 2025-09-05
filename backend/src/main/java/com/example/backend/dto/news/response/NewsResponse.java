package com.example.backend.dto.news.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    private List<NewsArticle> articles;
    private int totalCount;
    private String language;
    private LocalDateTime lastUpdated;
    private List<String> sources;
    private String searchQuery;
    private boolean fromCache;
    private String message;
}
