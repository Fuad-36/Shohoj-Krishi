package com.example.backend.dto.news.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {
    private String language = "en";
    private int limit = 10;
    private String category;
    private String source;
    private String searchQuery;
    private boolean includeContent = true;
    private boolean translateToLocal = true;
}
