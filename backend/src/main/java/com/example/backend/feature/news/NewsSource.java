package com.example.backend.feature.news;

public class NewsSource {
    private final String url;
    private final String articleSelector;
    private final String titleSelector;
    private final String summarySelector;
    private final String dateSelector;
    private final String urlAttribute;


    public NewsSource(String url, String articleSelector, String titleSelector,
                      String summarySelector, String dateSelector, String urlAttribute) {
        this.url = url;
        this.articleSelector = articleSelector;
        this.titleSelector = titleSelector;
        this.summarySelector = summarySelector;
        this.dateSelector = dateSelector;
        this.urlAttribute = urlAttribute;
    }


    public String getUrl() { return url; }
    public String getArticleSelector() { return articleSelector; }
    public String getTitleSelector() { return titleSelector; }
    public String getSummarySelector() { return summarySelector; }
    public String getDateSelector() { return dateSelector; }
    public String getUrlAttribute() { return urlAttribute; }
}
