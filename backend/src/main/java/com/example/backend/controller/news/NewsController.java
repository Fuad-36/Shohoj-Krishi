package com.example.backend.controller.news;


import com.example.backend.dto.news.request.NewsRequest;
import com.example.backend.dto.news.request.NewsSearchRequest;
import com.example.backend.dto.news.response.NewsArticle;
import com.example.backend.dto.news.response.NewsResponse;
import com.example.backend.service.news.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "News API", description = "Farming news scraping and translation service")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/farming")
    @Operation(summary = "Get latest farming news",
            description = "Fetches latest farming news from multiple sources and translates to requested language")
    public ResponseEntity<NewsResponse> getFarmingNews(
            @Parameter(description = "Language for news content (en/bn)")
            @RequestParam(defaultValue = "bn") String language,

            @Parameter(description = "Maximum number of articles to return")
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {

        log.info("GET /api/v1/news/farming - language: {}, limit: {}", language, limit);

        try {
            NewsResponse response = newsService.getFarmingNews(language, limit);

            if (response.getArticles().isEmpty()) {
                return ResponseEntity.ok(NewsResponse.builder()
                        .articles(List.of())
                        .totalCount(0)
                        .language(language)
                        .message("কোন খবর পাওয়া যায়নি। পরে আবার চেষ্টা করুন।")
                        .build());
            }

            log.info("Successfully fetched {} farming news articles", response.getArticles().size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching farming news: {}", e.getMessage(), e);
            return ResponseEntity.ok(NewsResponse.builder()
                    .articles(List.of())
                    .totalCount(0)
                    .language(language)
                    .message("খবর সংগ্রহ করতে সমস্যা হয়েছে। দয়া করে পরে আবার চেষ্টা করুন।")
                    .build());
        }
    }

    @PostMapping("/farming")
    @Operation(summary = "Get farming news with detailed request",
            description = "Fetches farming news with advanced filtering options")
    public ResponseEntity<NewsResponse> getFarmingNewsAdvanced(
            @Valid @RequestBody NewsRequest request) {

        log.info("POST /api/v1/news/farming - request: {}", request);

        try {
            NewsResponse response = newsService.getFarmingNews(
                    request.getLanguage(),
                    request.getLimit()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching farming news with advanced request: {}", e.getMessage(), e);
            return ResponseEntity.ok(NewsResponse.builder()
                    .articles(List.of())
                    .totalCount(0)
                    .language(request.getLanguage())
                    .message("খবর সংগ্রহ করতে সমস্যা হয়েছে। দয়া করে পরে আবার চেষ্টা করুন।")
                    .build());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search farming news",
            description = "Search farming news with specific keywords")
    public ResponseEntity<NewsResponse> searchFarmingNews(
            @Parameter(description = "Search query/keywords")
            @RequestParam String query,

            @Parameter(description = "Language for news content (en/bn)")
            @RequestParam(defaultValue = "bn") String language,

            @Parameter(description = "Maximum number of articles to return")
            @RequestParam(defaultValue = "10") @Min(1) @Max(30) int limit) {

        log.info("GET /api/v1/news/search - query: {}, language: {}, limit: {}", query, language, limit);

        try {
            NewsResponse response = newsService.searchFarmingNews(query, language, limit);

            if (response.getArticles().isEmpty()) {
                String message = "bn".equals(language) ?
                        "আপনার খোঁজার বিষয়ে কোন খবর পাওয়া যায়নি।" :
                        "No news found for your search query.";

                response.setMessage(message);
            }

            log.info("Search returned {} articles for query: {}", response.getArticles().size(), query);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error searching farming news: {}", e.getMessage(), e);
            return ResponseEntity.ok(NewsResponse.builder()
                    .articles(List.of())
                    .totalCount(0)
                    .language(language)
                    .searchQuery(query)
                    .message("খোঁজতে সমস্যা হয়েছে। দয়া করে পরে আবার চেষ্টা করুন।")
                    .build());
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Advanced search for farming news",
            description = "Search farming news with advanced filtering and options")
    public ResponseEntity<NewsResponse> searchFarmingNewsAdvanced(
            @Valid @RequestBody NewsSearchRequest request) {

        log.info("POST /api/v1/news/search - request: {}", request);

        try {
            NewsResponse response = newsService.searchFarmingNews(
                    request.getQuery(),
                    request.getLanguage(),
                    request.getLimit()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in advanced news search: {}", e.getMessage(), e);
            return ResponseEntity.ok(NewsResponse.builder()
                    .articles(List.of())
                    .totalCount(0)
                    .language(request.getLanguage())
                    .searchQuery(request.getQuery())
                    .message("খোঁজতে সমস্যা হয়েছে। দয়া করে পরে আবার চেষ্টা করুন।")
                    .build());
        }
    }

    @GetMapping("/sources")
    @Operation(summary = "Get available news sources",
            description = "Returns list of all available news sources")
    public ResponseEntity<List<String>> getNewsSources() {
        log.info("GET /api/v1/news/sources");

        try {
            List<String> sources = newsService.getAvailableSources();
            return ResponseEntity.ok(sources);
        } catch (Exception e) {
            log.error("Error getting news sources: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Check news service health",
            description = "Health check endpoint for news scraping service")
    public ResponseEntity<String> healthCheck() {
        log.info("GET /api/v1/news/health");
        return ResponseEntity.ok("News service is running");
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest single farming news",
            description = "Gets the most recent farming news article")
    public ResponseEntity<NewsArticle> getLatestNews(
            @Parameter(description = "Language for news content (en/bn)")
            @RequestParam(defaultValue = "bn") String language) {

        log.info("GET /api/v1/news/latest - language: {}", language);

        try {
            NewsResponse response = newsService.getFarmingNews(language, 1);

            if (response.getArticles().isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            NewsArticle latestArticle = response.getArticles().get(0);
            log.info("Retrieved latest news: {}", latestArticle.getTitle());

            return ResponseEntity.ok(latestArticle);

        } catch (Exception e) {
            log.error("Error fetching latest news: {}", e.getMessage(), e);
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get news summary",
            description = "Gets a brief summary of recent farming news")
    public ResponseEntity<String> getNewsSummary(
            @Parameter(description = "Language for summary (en/bn)")
            @RequestParam(defaultValue = "bn") String language,

            @Parameter(description = "Number of articles to summarize")
            @RequestParam(defaultValue = "5") @Min(1) @Max(10) int count) {

        log.info("GET /api/v1/news/summary - language: {}, count: {}", language, count);

        try {
            String summary = newsService.generateNewsSummary(language, count);
            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error generating news summary: {}", e.getMessage(), e);
            String errorMessage = "bn".equals(language) ?
                    "খবরের সারসংক্ষেপ তৈরি করতে সমস্যা হয়েছে।" :
                    "Unable to generate news summary.";
            return ResponseEntity.ok(errorMessage);
        }
    }
}
