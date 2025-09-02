package com.example.backend.service.chatbot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final RestTemplate restTemplate;

    private static final int MAX_FREE_QUERY_LENGTH = 500; // max characters for free services

    @PostConstruct
    public void init() {
        log.info("Using FREE translation services only");
    }

    public String translateToBengali(String englishText) {
        log.info(englishText);
        if (StringUtils.isBlank(englishText)) return "";

        if (containsBengaliScript(englishText)) return englishText;

        if (englishText.length() > MAX_FREE_QUERY_LENGTH) {

            log.warn("English Translation text exceeds free tier limit: {} chars", englishText.length());
            englishText = englishText.substring(0, MAX_FREE_QUERY_LENGTH); // truncate
        }

        return safeTranslate(englishText, "en", "bn");
    }

    public String translateToEnglish(String bengaliText) {
        log.info(bengaliText);
        if (StringUtils.isBlank(bengaliText)) return "";

        if (!containsBengaliScript(bengaliText)) return bengaliText;

        if (bengaliText.length() > MAX_FREE_QUERY_LENGTH) {
            log.warn("Bengali Translation text exceeds free tier limit: {} chars", bengaliText.length());
            bengaliText = bengaliText.substring(0, MAX_FREE_QUERY_LENGTH);
        }

        return safeTranslate(bengaliText, "bn", "en");
    }

    private String safeTranslate(String text, String sourceLanguage, String targetLanguage) {
        try {
            return translateWithMyMemoryService(text, sourceLanguage, targetLanguage);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error during translation: {}", e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Network error during translation: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected translation error: {}", e.getMessage());
        }

        log.warn("Falling back to original text due to translation failure.");
        return text;
    }

    private String translateWithFreeService(String text, String sourceLanguage, String targetLanguage) {
        try {
            Map<String, Object> request = Map.of(
                    "q", text,
                    "source", sourceLanguage,
                    "target", targetLanguage,
                    "format", "text"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://libretranslate.com/translate",
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object translatedText = response.getBody().get("translatedText");
                if (translatedText instanceof String) {
                    return (String) translatedText;
                } else {
                    log.warn("LibreTranslate returned unexpected format: {}", response.getBody());
                }
            } else {
                log.warn("LibreTranslate failed with status: {}", response.getStatusCode());
            }

            return translateWithMyMemoryService(text, sourceLanguage, targetLanguage);

        } catch (HttpClientErrorException e) {
            log.error("LibreTranslate HTTP error: {}", e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            log.error("LibreTranslate network error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("LibreTranslate unexpected error: {}", e.getMessage());
        }

        return translateWithMyMemoryService(text, sourceLanguage, targetLanguage);
    }

    private String translateWithMyMemoryService(String text, String sourceLanguage, String targetLanguage) {
        try {
            if (sourceLanguage.equals("bn") ) sourceLanguage+="-IN";
            if (targetLanguage.equals("bn") ) targetLanguage+="-IN";
            String langPair = sourceLanguage + "|" + targetLanguage;
            String url = String.format("https://api.mymemory.translated.net/get?q=%s&langpair=%s",
                    URLEncoder.encode(text, StandardCharsets.UTF_8),
                    langPair);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object responseDataObj = response.getBody().get("responseData");
                if (responseDataObj instanceof Map) {
                    Map<String, Object> responseData = (Map<String, Object>) responseDataObj;
                    Object translatedText = responseData.get("translatedText");
                    if (translatedText instanceof String) {
                        return (String) translatedText;
                    } else {
                        log.warn("MyMemory returned unexpected format: {}", responseData);
                    }
                }
            } else {
                log.warn("MyMemory translation failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("MyMemory translation error: {}", e.getMessage());
        }

        return text; // fallback to original text
    }

    private boolean containsBengaliScript(String text) {
        return text != null && text.matches(".*[\\u0980-\\u09FF].*");
    }
}
