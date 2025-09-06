package com.example.backend.service.news;


import com.example.backend.dto.news.response.NewsArticle;
import com.example.backend.dto.news.response.NewsResponse;
import com.example.backend.feature.news.NewsSource;
import com.example.backend.feature.news.NewsSourceRegistry;
import com.example.backend.service.chatbot.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
        import java.util.concurrent.*;
        import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

    private final TranslationService translationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Value("${news.scraping.timeout:10000}")
    private int scrapingTimeout;

    @Value("${news.scraping.user-agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36}")
    private String userAgent;

    // Use central registry for sources
    private final Map<String, NewsSource> newsSources = NewsSourceRegistry.getAllSources();

    @Cacheable(value = "farmingNews", key = "'farming-news-' + #language + '-' + #limit")
    public NewsResponse getFarmingNews(String language, int limit) {
        log.info("Fetching farming news in language: {} with limit: {}", language, limit);

        List<NewsArticle> allArticles = new ArrayList<>();
        List<CompletableFuture<List<NewsArticle>>> futures = new ArrayList<>();

        // Scrape from multiple sources concurrently
        for (Map.Entry<String, NewsSource> entry : newsSources.entrySet()) {
            CompletableFuture<List<NewsArticle>> future = CompletableFuture.supplyAsync(
                    () -> scrapeNewsFromSource(entry.getKey(), entry.getValue()),
                    executorService
            );
            futures.add(future);
        }

        // Wait for all sources to complete (with timeout)
        try {
            for (CompletableFuture<List<NewsArticle>> future : futures) {
                try {
                    List<NewsArticle> articles = future.get(15, TimeUnit.SECONDS);
                    allArticles.addAll(articles);
                } catch (TimeoutException e) {
                    log.warn("News source timed out");
                    future.cancel(true);
                } catch (Exception e) {
                    log.error("Error getting news from source: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error collecting news from sources: {}", e.getMessage());
        }

        // Sort by date (newest first) and limit
        List<NewsArticle> sortedArticles = allArticles.stream()
                .filter(Objects::nonNull)
                .sorted((a, b) -> b.getPublishedDate().compareTo(a.getPublishedDate()))
                .limit(limit)
                .collect(Collectors.toList());

        // Translate to Bengali if requested
        if ("bn".equals(language)) {
            sortedArticles = translateArticlesToBengali(sortedArticles);
        }

        return NewsResponse.builder()
                .articles(sortedArticles)
                .totalCount(sortedArticles.size())
                .language(language)
                .lastUpdated(LocalDateTime.now())
                .sources(new ArrayList<>(newsSources.keySet()))
                .build();
    }

    // --- rest of the methods remain unchanged from your original implementation ---

    private List<NewsArticle> scrapeNewsFromSource(String sourceName, NewsSource source) {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            log.info("Scraping news from: {} -> {}", sourceName, source.getUrl());

            Document doc = Jsoup.connect(source.getUrl())
                    .userAgent(userAgent)
                    .timeout(scrapingTimeout)
                    .get();

            Elements newsItems = doc.select(source.getArticleSelector());
            log.info("Found {} news items from {}", newsItems.size(), sourceName);

            for (Element item : newsItems.stream().limit(10).collect(Collectors.toList())) {
                try {
                    NewsArticle article = extractArticleFromElement(item, source, sourceName);
                    if (article != null && isRelevantToFarming(article)) {
                        articles.add(article);
                    }
                } catch (Exception e) {
                    log.warn("Error extracting article from {}: {}", sourceName, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error scraping from {}: {}", sourceName, e.getMessage());
        }

        log.info("Successfully scraped {} articles from {}", articles.size(), sourceName);
        return articles;
    }

    private NewsArticle extractArticleFromElement(Element item, NewsSource source, String sourceName) {
        try {
            // Extract title and URL
            Element titleElement = item.selectFirst(source.getTitleSelector());
            if (titleElement == null) return null;

            String title = titleElement.text().trim();
            String url = titleElement.attr(source.getUrlAttribute());

            // Make URL absolute if relative
            if (url.startsWith("/")) {
                url = getBaseUrl(source.getUrl()) + url;
            }

            // Extract summary
            String summary = "";
            Element summaryElement = item.selectFirst(source.getSummarySelector());
            if (summaryElement != null) {
                summary = summaryElement.text().trim();
            }

            // Extract date
            LocalDateTime publishedDate = LocalDateTime.now();
            Element dateElement = item.selectFirst(source.getDateSelector());
            if (dateElement != null) {
                publishedDate = parseDate(dateElement.text().trim());
            }

            // Get content from article page (limited to avoid long scraping times)
            String content = summary; // Use summary as default content
            if (!url.isEmpty() && summary.length() < 200) {
                content = scrapeArticleContent(url);
            }

            return NewsArticle.builder()
                    .title(title)
                    .summary(summary.isEmpty() ? content.substring(0, Math.min(content.length(), 200)) : summary)
                    .content(content)
                    .url(url)
                    .source(sourceName)
                    .publishedDate(publishedDate)
                    .language("en") // Default to English, will be translated if needed
                    .build();

        } catch (Exception e) {
            log.warn("Error extracting article: {}", e.getMessage());
            return null;
        }
    }

    private String scrapeArticleContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(5000) // Shorter timeout for individual articles
                    .get();

            // Try common content selectors
            String[] contentSelectors = {
                    "article p", ".content p", ".article-content p",
                    ".post-content p", ".entry-content p", "p"
            };

            for (String selector : contentSelectors) {
                Elements paragraphs = doc.select(selector);
                if (paragraphs.size() > 0) {
                    String content = paragraphs.stream()
                            .map(Element::text)
                            .limit(5) // Limit to first 5 paragraphs
                            .collect(Collectors.joining(" "));

                    if (content.length() > 100) {
                        return content.substring(0, Math.min(content.length(), 1000));
                    }
                }
            }

        } catch (Exception e) {
            log.debug("Could not scrape content from: {}", url);
        }

        return "";
    }

    private boolean isRelevantToFarming(NewsArticle article) {
        String text = (article.getTitle() + " " + article.getSummary() + " " + article.getContent()).toLowerCase();

        String[] farmingKeywords = {
                "agriculture", "farming", "crop", "farmer", "harvest", "soil", "seed", "plant",
                "livestock", "cattle", "dairy", "organic", "pesticide", "fertilizer", "irrigation",
                "agricultural", "rural", "food security", "sustainable", "agri", "cultivation"
        };

        return Arrays.stream(farmingKeywords)
                .anyMatch(text::contains);
    }

    private List<NewsArticle> translateArticlesToBengali(List<NewsArticle> articles) {
        log.info("Translating {} articles to Bengali", articles.size());

        return articles.parallelStream()
                .map(this::translateSingleArticle)
                .collect(Collectors.toList());
    }

    private NewsArticle translateSingleArticle(NewsArticle article) {
        try {
            String translatedTitle = translationService.translateToBengali(article.getTitle());
            String translatedSummary = translationService.translateToBengali(article.getSummary());

            // For content, translate only if it's not too long
            String translatedContent = article.getContent();
            if (translatedContent.length() < 2000) {
                translatedContent = translationService.translateToBengali(article.getContent());
            } else {
                // For very long content, translate just the first part
                String shortContent = translatedContent.substring(0, Math.min(1500, translatedContent.length()));
                translatedContent = translationService.translateToBengali(shortContent);
            }

            return NewsArticle.builder()
                    .title(translatedTitle)
                    .summary(translatedSummary)
                    .content(translatedContent)
                    .url(article.getUrl())
                    .source(article.getSource())
                    .publishedDate(article.getPublishedDate())
                    .language("bn")
                    .originalLanguage("en")
                    .build();

        } catch (Exception e) {
            log.error("Error translating article: {}", e.getMessage());
            // Return original article if translation fails
            return article;
        }
    }

    private LocalDateTime parseDate(String dateText) {
        try {
            // Try different date formats
            String[] formats = {
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd",
                    "MMM dd, yyyy",
                    "dd MMM yyyy",
                    "MMMM dd, yyyy"
            };

            for (String format : formats) {
                try {
                    return LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern(format));
                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {
            log.debug("Could not parse date: {}", dateText);
        }

        // Return current date if parsing fails
        return LocalDateTime.now().minusHours(1); // Assume recent
    }

    private String getBaseUrl(String fullUrl) {
        try {
            return fullUrl.substring(0, fullUrl.indexOf('/', 8)); // Find first '/' after protocol
        } catch (Exception e) {
            return fullUrl;
        }
    }

    // Alternative method to get news with search keywords
    public NewsResponse searchFarmingNews(String query, String language, int limit) {
        log.info("Searching farming news with query: {} in language: {}", query, language);

        // Get all news first
        NewsResponse allNews = getFarmingNews(language, limit * 2);

        // Filter by search query
        String searchQuery = query.toLowerCase();
        List<NewsArticle> filteredArticles = allNews.getArticles().stream()
                .filter(article -> {
                    String searchText = (article.getTitle() + " " +
                            article.getSummary() + " " +
                            article.getContent()).toLowerCase();
                    return searchText.contains(searchQuery);
                })
                .limit(limit)
                .collect(Collectors.toList());

        return NewsResponse.builder()
                .articles(filteredArticles)
                .totalCount(filteredArticles.size())
                .language(language)
                .lastUpdated(LocalDateTime.now())
                .sources(allNews.getSources())
                .searchQuery(query)
                .build();
    }

    // return list of available source names
    public List<String> getAvailableSources() {
        return new ArrayList<>(newsSources.keySet());
    }

    // simple summary generator: take top `count` article titles and join them.
// it returns Bengali summary if language == "bn" (translate titles via TranslationService).
    public String generateNewsSummary(String language, int count) {
        try {
            // get latest articles (small limit)
            NewsResponse resp = getFarmingNews("en", Math.max(count, 5)); // fetch in English
            List<NewsArticle> articles = resp.getArticles();

            if (articles.isEmpty()) {
                return "bn".equals(language) ? "কোন খবর পাওয়া যায়নি।" : "No news available.";
            }

            // build short bullet list of top `count` titles
            String summary = articles.stream()
                    .limit(count)
                    .map(a -> "- " + a.getTitle())
                    .collect(Collectors.joining("\n"));

            // translate if requested
            if ("bn".equals(language)) {
                // translate whole summary text at once
                try {
                    return translationService.translateToBengali(summary);
                } catch (Exception e) {
                    log.warn("Translation for summary failed, returning English summary");
                }
            }
            return summary;
        } catch (Exception e) {
            log.error("Failed to generate news summary: {}", e.getMessage(), e);
            return "bn".equals(language) ? "সংক্ষিপ্তসার তৈরি করতে সমস্যা হয়েছে।" : "Unable to generate news summary.";
        }
    }


}
