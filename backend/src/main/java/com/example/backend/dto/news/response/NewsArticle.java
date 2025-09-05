package com.example.backend.dto.news.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {
    private String title;
    private String summary;
    private String content;
    private String url;
    private String source;
    private LocalDateTime publishedDate;
    private String language;
    private String originalLanguage;
    private String imageUrl;
    private String[] tags;
    private String category;
    private int readTime; // estimated reading time in minutes
}
