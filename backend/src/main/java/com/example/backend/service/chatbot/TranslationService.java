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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final RestTemplate restTemplate;

    private static final int MAX_FREE_QUERY_LENGTH = 400; // Reduced for safety margin
    private static final int MIN_CHUNK_SIZE = 50; // Minimum chunk size to avoid tiny fragments

    @PostConstruct
    public void init() {
        log.info("Using FREE translation services with chunking support");
    }

    public String translateToBengali(String englishText) {
        log.info("Translating to Bengali: {} chars", englishText != null ? englishText.length() : 0);
        if (StringUtils.isBlank(englishText)) return "";

        if (containsBengaliScript(englishText)) {
            log.info("Text already contains Bengali script, returning as-is");
            return englishText;
        }

        return translateWithChunking(englishText, "en", "bn");
    }

    public String translateToEnglish(String bengaliText) {
        log.info("Translating to English: {} chars", bengaliText != null ? bengaliText.length() : 0);
        if (StringUtils.isBlank(bengaliText)) return "";

        if (!containsBengaliScript(bengaliText)) {
            log.info("Text doesn't contain Bengali script, returning as-is");
            return bengaliText;
        }

        return translateWithChunking(bengaliText, "bn", "en");
    }

    /**
     * Translates text by breaking it into chunks if it's too long
     */
    private String translateWithChunking(String text, String sourceLanguage, String targetLanguage) {
        if (text.length() <= MAX_FREE_QUERY_LENGTH) {
            // Text is small enough, translate directly
            return safeTranslate(text, sourceLanguage, targetLanguage);
        }

        log.info("Text is too long ({} chars), breaking into chunks", text.length());

        // Break text into chunks
        List<String> chunks = createIntelligentChunks(text);
        List<String> translatedChunks = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            log.info("Translating chunk {}/{}: {} chars", i + 1, chunks.size(), chunk.length());

            String translatedChunk = safeTranslate(chunk, sourceLanguage, targetLanguage);
            translatedChunks.add(translatedChunk);

            // Add small delay between requests to be respectful to free APIs
            if (i < chunks.size() - 1) {
                try {
                    Thread.sleep(200); // 200ms delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Translation interrupted");
                    break;
                }
            }
        }

        String result = String.join(" ", translatedChunks);
        log.info("Chunked translation completed: {} chars -> {} chars", text.length(), result.length());
        return result;
    }

    /**
     * Creates intelligent chunks that try to preserve sentence and word boundaries
     */
    private List<String> createIntelligentChunks(String text) {
        List<String> chunks = new ArrayList<>();

        // First, try to split by sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            // If adding this sentence would exceed the limit, save current chunk and start new one
            if (currentChunk.length() + sentence.length() + 1 > MAX_FREE_QUERY_LENGTH) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }

                // If single sentence is still too long, split by words
                if (sentence.length() > MAX_FREE_QUERY_LENGTH) {
                    chunks.addAll(splitLongSentence(sentence));
                } else {
                    currentChunk.append(sentence);
                }
            } else {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
            }
        }

        // Add remaining chunk
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        // Filter out chunks that are too small (unless it's the only chunk)
        if (chunks.size() > 1) {
            chunks = chunks.stream()
                    .filter(chunk -> chunk.length() >= MIN_CHUNK_SIZE)
                    .collect(Collectors.toList());
        }

        log.info("Created {} chunks from text of {} chars", chunks.size(), text.length());
        return chunks;
    }

    /**
     * Splits a very long sentence by words when it can't fit in a single chunk
     */
    private List<String> splitLongSentence(String sentence) {
        List<String> chunks = new ArrayList<>();
        String[] words = sentence.split("\\s+");

        StringBuilder currentChunk = new StringBuilder();

        for (String word : words) {
            if (currentChunk.length() + word.length() + 1 > MAX_FREE_QUERY_LENGTH) {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }
            }

            if (currentChunk.length() > 0) {
                currentChunk.append(" ");
            }
            currentChunk.append(word);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
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
            if (sourceLanguage.equals("bn")) sourceLanguage += "-IN";
            if (targetLanguage.equals("bn")) targetLanguage += "-IN";
            String langPair = sourceLanguage + "|" + targetLanguage;

            // Encode the text properly
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = String.format("https://api.mymemory.translated.net/get?q=%s&langpair=%s",
                    encodedText, langPair);

            log.debug("Calling MyMemory API with langpair: {}", langPair);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("MyMemory response received");

                Object responseDataObj = response.getBody().get("responseData");
                if (responseDataObj instanceof Map) {
                    Map<String, Object> responseData = (Map<String, Object>) responseDataObj;
                    Object translatedText = responseData.get("translatedText");

                    if (translatedText instanceof String) {
                        String result = (String) translatedText;

                        log.debug("Raw translation result: {}", result);

                        // Clean up the translation result
                        result = cleanupEncodingIssues(result);

                        log.debug("Cleaned translation result: {}", result);

                        // Basic validation - check if result is reasonable
                        if (isValidTranslation(result, text)) {
                            return result;
                        } else {
                            log.warn("Translation result appears to be invalid, returning original text");
                            return text;
                        }
                    } else {
                        log.warn("MyMemory returned unexpected translatedText format: {}", responseData);
                    }
                } else {
                    log.warn("MyMemory returned unexpected responseData format");
                }
            } else {
                log.warn("MyMemory translation failed with status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("MyMemory translation error: {}", e.getMessage());
        }

        log.debug("Returning original text as fallback");
        return text; // fallback to original text
    }

    /**
     * Comprehensive cleanup of encoding issues found in translations
     */
    private String cleanupEncodingIssues(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        log.debug("Starting cleanup of: {}", text);

        try {
            // First attempt: Try standard URL decoding if it looks properly encoded
            if (text.matches(".*%[0-9A-Fa-f]{2}.*")) {
                try {
                    String decoded = URLDecoder.decode(text, StandardCharsets.UTF_8);
                    log.debug("URL decoded once: {}", decoded);

                    // Sometimes need multiple rounds of decoding
                    if (decoded.matches(".*%[0-9A-Fa-f]{2}.*")) {
                        decoded = URLDecoder.decode(decoded, StandardCharsets.UTF_8);
                        log.debug("URL decoded twice: {}", decoded);
                    }
                    text = decoded;
                } catch (Exception e) {
                    log.debug("URL decoding failed, continuing with manual cleanup: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.debug("Initial decoding attempt failed: {}", e.getMessage());
        }

        // Manual cleanup of specific problematic patterns found in your data

        // Common URL encoded characters
        text = text.replace("%E2%80%99", "'");  // Right single quotation mark
        text = text.replace("%E2%80%98", "'");  // Left single quotation mark
        text = text.replace("%E2%80%A6", "…");  // Horizontal ellipsis
        text = text.replace("%E2%80%93", "–");  // En dash
        text = text.replace("%E2%80%94", "—");  // Em dash
        text = text.replace("%20", " ");        // Space
        text = text.replace("%21", "!");        // Exclamation mark
        text = text.replace("%22", "\"");       // Quotation mark
        text = text.replace("%23", "#");        // Hash
        text = text.replace("%24", "$");        // Dollar sign
        text = text.replace("%25", "%");        // Percent sign
        text = text.replace("%26", "&");        // Ampersand
        text = text.replace("%27", "'");        // Apostrophe
        text = text.replace("%28", "(");        // Opening parenthesis
        text = text.replace("%29", ")");        // Closing parenthesis
        text = text.replace("%2A", "*");        // Asterisk
        text = text.replace("%2B", "+");        // Plus sign
        text = text.replace("%2C", ",");        // Comma
        text = text.replace("%2D", "-");        // Hyphen
        text = text.replace("%2E", ".");        // Period
        text = text.replace("%2F", "/");        // Forward slash
        text = text.replace("%3A", ":");        // Colon
        text = text.replace("%3B", ";");        // Semicolon
        text = text.replace("%3C", "<");        // Less than
        text = text.replace("%3D", "=");        // Equals sign
        text = text.replace("%3E", ">");        // Greater than
        text = text.replace("%3F", "?");        // Question mark
        text = text.replace("%40", "@");        // At sign

        // Handle malformed patterns with spaces (like "% 28" instead of "%28")
        text = text.replaceAll("%\\s*28", "(");
        text = text.replaceAll("%\\s*29", ")");
        text = text.replaceAll("%\\s*2C", ",");
        text = text.replaceAll("%\\s*20", " ");
        text = text.replaceAll("%\\s*21", "!");
        text = text.replaceAll("%\\s*22", "\"");
        text = text.replaceAll("%\\s*23", "#");
        text = text.replaceAll("%\\s*24", "$");
        text = text.replaceAll("%\\s*25", "%");
        text = text.replaceAll("%\\s*26", "&");
        text = text.replaceAll("%\\s*27", "'");

        // Handle partial encoding corruption like "2C500" -> remove the stray "2C"
        text = text.replaceAll("(?<!\\d)2C(?=\\d)", "");

        // Clean up patterns like "%E2%80%এ 6" (corrupted multibyte sequences)
        text = text.replaceAll("%E2%80%([\\u0980-\\u09FF])\\s*\\d+", "$1");

        // Remove any remaining malformed percent encoding
        text = text.replaceAll("%[^0-9A-Fa-f]", "");
        text = text.replaceAll("%[0-9A-Fa-f][^0-9A-Fa-f](?![0-9A-Fa-f])", "");
        text = text.replaceAll("%(?![0-9A-Fa-f]{2})", "");

        // Clean up extra spaces
        text = text.replaceAll("\\s+", " ").trim();

        log.debug("Final cleaned result: {}", text);
        return text;
    }

    /**
     * Validate if the translation result is reasonable
     */
    private boolean isValidTranslation(String translation, String originalText) {
        if (translation == null || translation.trim().isEmpty()) {
            return false;
        }

        // Check for excessive length (might indicate encoding issues)
        if (translation.length() > originalText.length() * 5) {
            return false;
        }

        // Check for remaining URL encoding artifacts
        if (translation.matches(".*%[0-9A-Fa-f]{2}.*")) {
            log.warn("Translation still contains URL encoding artifacts");
            return false;
        }

        // Check for partial encoding artifacts
        if (translation.matches(".*%\\s.*") || translation.matches(".*%[^0-9A-Fa-f].*")) {
            log.warn("Translation contains malformed encoding artifacts");
            return false;
        }

        // Check if it's just whitespace or control characters
        if (translation.trim().length() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Clean up any remaining encoding artifacts in the translation
     */
    private String cleanupTranslation(String text) {
        if (text == null) return null;

        // Remove partial URL encoding artifacts like "% 2" or "% E"
        text = text.replaceAll("%\\s+\\w", " ");

        // Clean up common malformed patterns
        text = text.replaceAll("%\\s*28", "(");  // %28 -> (
        text = text.replaceAll("%\\s*29", ")");  // %29 -> )
        text = text.replaceAll("%\\s*2C", ",");  // %2C -> ,

        // Clean up any remaining isolated % signs
        text = text.replaceAll("%(?![0-9A-Fa-f]{2})", "");

        // Normalize multiple spaces
        text = text.replaceAll("\\s+", " ").trim();

        return text;
    }

    /**
     * Check if a string contains valid URL encoding patterns
     */


    /**
     * Validate if the translation result is reasonable
     */


    private boolean containsBengaliScript(String text) {
        return text != null && text.matches(".*[\\u0980-\\u09FF].*");
    }

    /**
     * Utility method to check if text needs translation
     */
    public boolean needsTranslation(String text, String targetLanguage) {
        if (StringUtils.isBlank(text)) return false;

        if ("bn".equals(targetLanguage)) {
            return !containsBengaliScript(text);
        } else if ("en".equals(targetLanguage)) {
            return containsBengaliScript(text);
        }

        return false;
    }

    /**
     * Get estimated number of chunks for a given text
     */
    public int getEstimatedChunkCount(String text) {
        if (StringUtils.isBlank(text)) return 0;
        return (int) Math.ceil((double) text.length() / MAX_FREE_QUERY_LENGTH);
    }
}