package com.example.backend.feature.news;

import java.util.Map;

public final class NewsSourceRegistry {


    private NewsSourceRegistry() {}


    public static Map<String, NewsSource> getAllSources() {
        return Map.ofEntries(
                Map.entry("FAO News", new NewsSource(
                        "https://www.fao.org/news/en/",
                        ".news-item",
                        ".title a",
                        ".summary",
                        ".date",
                        "href"
                )),


                Map.entry("World Bank Agriculture", new NewsSource(
                        "https://www.worldbank.org/en/topic/agriculture",
                        ".list-item",
                        "h3 a",
                        ".summary",
                        ".date",
                        "href"
                )),


                Map.entry("Agriculture.com", new NewsSource(
                        "https://www.agriculture.com/news",
                        "article",
                        "h2 a, h3 a",
                        ".summary, .excerpt",
                        ".date, time",
                        "href"
                )),


                Map.entry("Successful Farming", new NewsSource(
                        "https://www.agriculture.com/successful-farming/news",
                        ".article-card",
                        ".article-title a",
                        ".article-summary",
                        ".article-date",
                        "href"
                )),


// Bangladesh sources (English + Bangla)
                Map.entry("Krishi Jagran", new NewsSource(
                        "https://krishijagran.com",
                        ".post, article",
                        "h2 a, .entry-title a",
                        ".excerpt, .summary",
                        ".date, time",
                        "href"
                )),


                Map.entry("Down To Earth - Agriculture", new NewsSource(
                        "https://www.downtoearth.org.in/topic/agriculture",
                        ".post, article",
                        "h2 a, .title a",
                        ".summary, .lead",
                        ".date",
                        "href"
                )),


                Map.entry("Global Agriculture", new NewsSource(
                        "https://www.global-agriculture.com/",
                        ".post, article",
                        "h2 a, .title a",
                        ".summary, .lead",
                        ".date",
                        "href"
                )),


                Map.entry("iGrow News", new NewsSource(
                        "https://igrownews.com/agriculture-news",
                        ".post, article",
                        "h2 a, .title a",
                        ".excerpt, .summary",
                        ".date",
                        "href"
                )),


                Map.entry("DD News - Agriculture", new NewsSource(
                        "https://ddnews.gov.in/en/category/agriculture",
                        ".view-content, article",
                        "h2 a, .title a",
                        ".summary",
                        ".date",
                        "href"
                )),


                Map.entry("Indian Express - Agriculture", new NewsSource(
                        "https://indianexpress.com/section/agriculture/",
                        ".article, .listing",
                        "h2 a, .title a",
                        ".synopsis, .excerpt",
                        ".date, time",
                        "href"
                )),


                Map.entry("Farmers Weekly", new NewsSource(
                        "https://www.fwi.co.uk",
                        ".listing, article",
                        "h2 a, .title a",
                        ".summary, .teaser",
                        ".date",
                        "href"
                ))
        );
    }
}
