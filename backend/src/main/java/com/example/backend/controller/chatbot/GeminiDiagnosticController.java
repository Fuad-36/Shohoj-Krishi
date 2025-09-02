package com.example.backend.controller.chatbot;


import com.example.backend.config.ChatbotConfig;
import com.example.backend.dto.chatbot.response.FarmerContext;
import com.example.backend.service.chatbot.FarmerDataService;
import com.example.backend.service.chatbot.TranslationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Updated Diagnostic Controller for Gemini API troubleshooting (Blocking version)
@RestController
@RequestMapping("/api/chatbot/debug")
@RequiredArgsConstructor
@Slf4j
public class GeminiDiagnosticController {

    private final ChatbotConfig config;
    private final TranslationService translationService;
    private final FarmerDataService farmerDataService;
    private RestTemplate geminiRestTemplate;
    private RestTemplate freeTranslationRestTemplate;

    @PostConstruct
    public void init() {
        // Initialize Gemini RestTemplate with longer timeouts for diagnostics
        SimpleClientHttpRequestFactory geminiFactory = new SimpleClientHttpRequestFactory();
        geminiFactory.setConnectTimeout(30000); // 30 seconds
        geminiFactory.setReadTimeout(60000); // 60 seconds

        this.geminiRestTemplate = new RestTemplate(geminiFactory);

        // Configure message converters for large payloads
        this.geminiRestTemplate.getMessageConverters().forEach(converter -> {
            if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
                ((org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) converter)
                        .setDefaultCharset(java.nio.charset.StandardCharsets.UTF_8);
            }
        });

        // Initialize free translation RestTemplate
        SimpleClientHttpRequestFactory translationFactory = new SimpleClientHttpRequestFactory();
        translationFactory.setConnectTimeout(20000); // 20 seconds
        translationFactory.setReadTimeout(30000); // 30 seconds

        this.freeTranslationRestTemplate = new RestTemplate(translationFactory);

        log.info("Initialized GeminiDiagnosticController with REST clients (JDK HttpURLConnection)");
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            Map<String, Object> configInfo = Map.of(
                    "hasGeminiApiKey", config.getGeminiApiKey() != null && !config.getGeminiApiKey().isEmpty(),
                    "apiKeyLength", config.getGeminiApiKey() != null ? config.getGeminiApiKey().length() : 0,
                    "geminiModel", config.getGeminiModel(),
                    "geminiBaseUrl", config.getGeminiBaseUrl(),
                    "maxTokens", config.getMaxTokens(),
                    "temperature", config.getTemperature(),
                    "maxContextLength", config.getMaxContextLength(),
                    "safetyLevel", config.getSafetyLevel(),
                    "serviceType", "Google Gemini API (FREE)",
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(configInfo);
        } catch (Exception e) {
            log.error("Failed to retrieve config info: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now()));
        }
    }

    @PostMapping("/test-gemini-connection")
    public ResponseEntity<Map<String, Object>> testGeminiConnection() {
        try {
            if (config.getGeminiApiKey() == null || config.getGeminiApiKey().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "error", "No Gemini API key configured",
                        "instructions", "Get free API key from https://makersuite.google.com/app/apikey",
                        "timestamp", LocalDateTime.now()
                ));
            }

            log.info("Testing Gemini API connection...");

            // Test with simple Gemini request
            Map<String, Object> testRequest = createSimpleGeminiRequest("আপনি আমাকে কৃষি কাজে সাহায্য করতে পারবেন?");
            String uri = config.getGeminiBaseUrl() + "/" + config.getGeminiModel() +
                    ":generateContent?key=" + config.getGeminiApiKey();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(testRequest, headers);

            ResponseEntity<Map> response = geminiRestTemplate.postForEntity(uri, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Gemini API test successful: {}", response.getBody());

                // Extract the actual response text
                String responseText = extractGeminiResponseText(response.getBody());

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "model", config.getGeminiModel(),
                        "apiEndpoint", uri,
                        "responseText", responseText,
                        "fullResponse", response.getBody(),
                        "cost", "FREE - No charges applied",
                        "timestamp", LocalDateTime.now()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "error", "Unsuccessful response: " + response.getStatusCode(),
                        "timestamp", LocalDateTime.now()
                ));
            }

        } catch (Exception error) {
            log.error("Gemini API connection failed: {}", error.getMessage(), error);

            String errorAdvice = getGeminiErrorAdvice(error.getMessage());

            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", error.getMessage(),
                    "advice", errorAdvice,
                    "apiKeyStatus", config.getGeminiApiKey().length() > 0 ? "Present" : "Missing",
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/test-translation")
    public ResponseEntity<Map<String, Object>> testTranslation(@RequestParam String text) {
        try {
            log.info("Testing FREE translation services with text: {}", text);

            String toBengali = translationService.translateToBengali(text);
            String toEnglish = translationService.translateToEnglish(text);

            Map<String, Object> result = new HashMap<>();
            result.put("originalText", text);
            result.put("toBengali", toBengali);
            result.put("toEnglish", toEnglish);
            result.put("containsBengali", text.matches(".*[\\u0980-\\u09FF].*"));
            result.put("translationService", "LibreTranslate (FREE)");
            result.put("backupService", "MyMemory API (FREE)");
            result.put("cost", "FREE - No API keys required");
            result.put("success", true);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Translation test failed: {}", e.getMessage(), e);

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("originalText", text);
            errorResult.put("toBengali", "Translation to Bengali failed");
            errorResult.put("toEnglish", "Translation to English failed");
            errorResult.put("success", false);
            errorResult.put("error", e.getMessage());
            errorResult.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(errorResult);
        }
    }

    @PostMapping("/test-free-translation-direct")
    public ResponseEntity<Map<String, Object>> testFreeTranslationDirect(
            @RequestParam String text,
            @RequestParam String fromLang,
            @RequestParam String toLang) {

        try {
            log.info("Testing direct free translation: {} -> {}", fromLang, toLang);

            String libreTranslation = "LibreTranslate failed";
            String myMemoryTranslation = "MyMemory failed";

            // Test LibreTranslate directly
            try {
                Map<String, Object> libreRequest = Map.of(
                        "q", text,
                        "source", fromLang,
                        "target", toLang,
                        "format", "text"
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(libreRequest, headers);

                ResponseEntity<Map> response = freeTranslationRestTemplate.postForEntity(
                        "https://libretranslate.de/translate", entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    libreTranslation = (String) response.getBody().get("translatedText");
                    if (libreTranslation == null) libreTranslation = "LibreTranslate returned null";
                }
            } catch (Exception e) {
                log.warn("LibreTranslate direct test failed: {}", e.getMessage());
                libreTranslation = "LibreTranslate failed: " + e.getMessage();
            }

            // Test MyMemory as backup
            try {
                if(fromLang.equals("bn")) fromLang="bn-IN";
                if(toLang.equals("bn")) toLang="bn-IN";
                String langPair = fromLang + "|" + toLang;
                String url = UriComponentsBuilder.fromHttpUrl("https://api.mymemory.translated.net/get")
                        .queryParam("q", text)
                        .queryParam("langpair", langPair)
                        .toUriString();

                ResponseEntity<Map> response = freeTranslationRestTemplate.getForEntity(url, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("responseData");
                    if (responseData != null) {
                        myMemoryTranslation = (String) responseData.get("translatedText");
                        if (myMemoryTranslation == null) myMemoryTranslation = "MyMemory returned null";
                    }
                }
            } catch (Exception e) {
                log.warn("MyMemory direct test failed: {}", e.getMessage());
                myMemoryTranslation = "MyMemory failed: " + e.getMessage();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("originalText", text);
            result.put("fromLang", fromLang);
            result.put("toLang", toLang);
            result.put("libreTranslateResult", libreTranslation);
            result.put("myMemoryResult", myMemoryTranslation);
            result.put("cost", "COMPLETELY FREE");
            result.put("success", true);
            result.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Direct translation test failed: {}", e.getMessage(), e);

            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/test-full-chat-flow")
    public ResponseEntity<Map<String, Object>> testFullChatFlow(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam(defaultValue = "bn") String language) {

        log.info("Testing full FREE chat flow for user {}", userId);

        Map<String, Object> diagnostics = new HashMap<>();
        diagnostics.put("userId", userId);
        diagnostics.put("originalMessage", message);
        diagnostics.put("language", language);
        diagnostics.put("timestamp", LocalDateTime.now());

        try {
            // Get farmer context
            Optional<FarmerContext> farmerContext = farmerDataService.getFarmerContext(userId);
            diagnostics.put("farmerContextFound", farmerContext.isPresent());

            if (farmerContext.isPresent()) {
                diagnostics.put("farmerContext", farmerContext.get());
            }

            // Step 1: Test translation to English
            String englishText = translationService.translateToEnglish(message);
            diagnostics.put("step1_translation", englishText);
            log.info("Step 1 - Translated to English: {}", englishText);

            // Step 2: Build prompt
            String prompt = buildTestPrompt(englishText, farmerContext.orElse(null));
            diagnostics.put("step2_prompt", prompt);
            log.info("Step 2 - Built prompt with {} characters", prompt.length());

            // Step 3: Call Gemini
            String aiResponse = callGeminiForTest(prompt);
            diagnostics.put("step3_gemini_response", aiResponse);
            log.info("Step 3 - Gemini response: {}", aiResponse);

            // Step 4: Translate back to Bengali if needed
            String finalResponse;
            if ("bn".equals(language)) {
                finalResponse = translationService.translateToBengali(aiResponse);
                diagnostics.put("step4_final_response", finalResponse);
                log.info("Step 4 - Final Bengali response: {}", finalResponse);
            } else {
                finalResponse = aiResponse;
                diagnostics.put("step4_final_response", finalResponse);
            }

            diagnostics.put("success", true);
            diagnostics.put("totalSteps", 4);
            diagnostics.put("cost", "COMPLETELY FREE");

            return ResponseEntity.ok(diagnostics);

        } catch (Exception error) {
            log.error("Full chat flow test failed: {}", error.getMessage(), error);
            diagnostics.put("success", false);
            diagnostics.put("error", error.getMessage());
            diagnostics.put("failedAt", "See logs for step details");
            return ResponseEntity.ok(diagnostics);
        }
    }

    @GetMapping("/test-farmer-context")
    public ResponseEntity<Map<String, Object>> testFarmerContext(@RequestParam Long userId) {
        try {
            log.info("Testing farmer context retrieval for user {}", userId);

            Optional<FarmerContext> context = farmerDataService.getFarmerContext(userId);

            if (context.isPresent()) {
                FarmerContext farmerCtx = context.get();
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "context", farmerCtx,
                        "farmerName", farmerCtx.getFarmerName(),
                        "location", farmerCtx.getLocation(),
                        "currentCropsCount", farmerCtx.getCurrentCrops().size(),
                        "recentPostsCount", farmerCtx.getRecentPosts().size(),
                        "farmSize", farmerCtx.getFarmSize(),
                        "expertise", farmerCtx.getExpertise(),
                        "timestamp", LocalDateTime.now()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "No farmer context found for user " + userId,
                        "advice", "Make sure user has created a profile and made some posts",
                        "timestamp", LocalDateTime.now()
                ));
            }
        } catch (Exception e) {
            log.error("Farmer context test failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @PostMapping("/test-gemini-simple")
    public ResponseEntity<Map<String, Object>> testGeminiSimple(@RequestParam String message) {
        try {
            log.info("Testing simple Gemini API call with message: {}", message);

            String simplePrompt = "You are a helpful farming assistant for Bangladesh. Give a brief answer (max 50 words): " + message;

            String response = callGeminiForTest(simplePrompt);

            return ResponseEntity.ok(Map.<String, Object>of(
                    "success", true,
                    "originalMessage", message,
                    "prompt", simplePrompt,
                    "geminiResponse", response,
                    "model", config.getGeminiModel(),
                    "cost", "FREE",
                    "timestamp", LocalDateTime.now()
            ));

        } catch (Exception error) {
            log.error("Simple Gemini test failed: {}", error.getMessage(), error);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "originalMessage", message,
                    "error", error.getMessage(),
                    "advice", getGeminiErrorAdvice(error.getMessage()),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @GetMapping("/test-all-services")
    public ResponseEntity<Map<String, Object>> testAllServices() {
        try {
            log.info("Running comprehensive test of all FREE services");

            Map<String, Object> results = new HashMap<>();
            results.put("testStartTime", LocalDateTime.now());

            // Test 1: Config validation
            boolean configValid = validateConfig();
            results.put("configValid", configValid);

            if (!configValid) {
                results.put("success", false);
                results.put("error", "Configuration validation failed");
                results.put("testEndTime", LocalDateTime.now());
                return ResponseEntity.ok(results);
            }

            // Test 2: Translation services
            Map<String, Object> translationResults = testTranslationServices();
            results.put("translationTest", translationResults);

            // Test 3: Gemini API
            Map<String, Object> geminiResults = testGeminiAPI();
            results.put("geminiTest", geminiResults);

            results.put("success", true);
            results.put("testEndTime", LocalDateTime.now());
            results.put("allServicesCost", "COMPLETELY FREE");
            results.put("summary", "All tests completed");

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            log.error("Comprehensive test failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Comprehensive test failed: " + e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }



    @PostMapping("/diagnose-chat-issue")
    public ResponseEntity<Map<String, Object>> diagnoseChatIssue(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam(defaultValue = "GENERAL_FARMING") String chatType) {

        log.info("Diagnosing chat issue for user {} with message: {}", userId, message);

        Map<String, Object> diagnosis = new HashMap<>();
        diagnosis.put("userId", userId);
        diagnosis.put("message", message);
        diagnosis.put("chatType", chatType);
        diagnosis.put("timestamp", LocalDateTime.now());

        try {
            // Check 1: Farmer context
            Optional<FarmerContext> context = farmerDataService.getFarmerContext(userId);
            diagnosis.put("farmerContextAvailable", context.isPresent());

            // Check 2: Message validation
            diagnosis.put("messageLength", message.length());
            diagnosis.put("containsBengali", message.matches(".*[\\u0980-\\u09FF].*"));
            diagnosis.put("messageEmpty", message.trim().isEmpty());

            if (message.trim().isEmpty()) {
                diagnosis.put("issue", "Empty message");
                diagnosis.put("solution", "Provide a non-empty message");
                return ResponseEntity.ok(diagnosis);
            }

            // Check 3: Try translation
            String translated = translationService.translateToEnglish(message);
            diagnosis.put("translationWorking", true);
            diagnosis.put("translatedMessage", translated);

            // Check 4: Try Gemini API
            String testPrompt = "Answer briefly: " + translated;
            String geminiResponse = callGeminiForTest(testPrompt);
            diagnosis.put("geminiWorking", true);
            diagnosis.put("geminiResponse", geminiResponse);

            // Check 5: Try translation back
            String finalResponse = translationService.translateToBengali(geminiResponse);
            diagnosis.put("backTranslationWorking", true);
            diagnosis.put("finalResponse", finalResponse);

            diagnosis.put("overallSuccess", true);
            diagnosis.put("diagnosis", "All systems working correctly");
            diagnosis.put("recommendation", "Chat should work normally");

            return ResponseEntity.ok(diagnosis);

        } catch (Exception error) {
            log.error("Chat issue diagnosis failed: {}", error.getMessage(), error);
            diagnosis.put("overallSuccess", false);
            diagnosis.put("error", error.getMessage());
            diagnosis.put("recommendation", getErrorRecommendation(error.getMessage()));
            return ResponseEntity.ok(diagnosis);
        }
    }

    // Helper methods
    private boolean validateConfig() {
        if (config.getGeminiApiKey() == null || config.getGeminiApiKey().trim().isEmpty()) {
            log.error("Gemini API key is missing");
            return false;
        }

        if (config.getGeminiModel() == null || config.getGeminiModel().trim().isEmpty()) {
            log.error("Gemini model is not configured");
            return false;
        }

        return true;
    }

    private Map<String, Object> testTranslationServices() {
        try {
            String englishToBengali = translationService.translateToBengali("Hello farmer");
            String bengaliToEnglish = translationService.translateToEnglish("কৃষক ভাই");

            Map<String, Object> result = new HashMap<>();
            result.put("englishToBengali", englishToBengali);
            result.put("bengaliToEnglish", bengaliToEnglish);
            result.put("working", true);
            result.put("service", "FREE translation services");
            return result;

        } catch (Exception e) {
            log.error("Translation services test failed: {}", e.getMessage(), e);
            return new HashMap<>(Map.of(
                    "working", false,
                    "error", "Translation services failed: " + e.getMessage()
            ));
        }
    }

    private Map<String, Object> testGeminiAPI() {
        try {
            String testPrompt = "Say hello to Bangladeshi farmers in one sentence.";
            String response = callGeminiForTest(testPrompt);

            Map<String, Object> result = new HashMap<>();
            result.put("working", true);
            result.put("response", response);
            result.put("model", config.getGeminiModel());
            result.put("cost", "FREE");
            return result;

        } catch (Exception e) {
            log.error("Gemini API test failed: {}", e.getMessage(), e);
            return new HashMap<>(Map.of(
                    "working", false,
                    "error", "Gemini API failed: " + e.getMessage()
            ));
        }
    }

    private String callGeminiForTest(String prompt) {
        Map<String, Object> request = createSimpleGeminiRequest(prompt);
        String uri = config.getGeminiBaseUrl() + "/" + config.getGeminiModel() +
                ":generateContent?key=" + config.getGeminiApiKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = geminiRestTemplate.postForEntity(uri, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return extractGeminiResponseText(response.getBody());
        } else {
            throw new RuntimeException("Gemini API returned status: " + response.getStatusCode());
        }
    }

    private Map<String, Object> createSimpleGeminiRequest(String prompt) {
        return Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt)),
                        "role", "user"
                )),
                "generationConfig", Map.of(
                        "temperature", config.getTemperature(),
                        "maxOutputTokens", Math.min(config.getMaxTokens(), 8192), // Keep it small for testing
                        "topP", 0.8,
                        "topK", 40
                ),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
                )
        );
    }

    private String extractGeminiResponseText(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);

                // Log finish reason if available
                String finishReason = (String) candidate.get("finishReason");
                if ("MAX_TOKENS".equals(finishReason)) {
                    return "Model stopped because max tokens were reached — try increasing maxOutputTokens.";
                }

                if ("SAFETY".equals(finishReason)) {
                    return "Response blocked due to safety filters.";
                }

                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                if (content != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        Object text = parts.get(0).get("text");
                        if (text != null) {
                            return text.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract text from Gemini response: {}", e.getMessage(), e);
        }
        return "Response extraction failed";
    }

    private String buildTestPrompt(String message, FarmerContext context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful farming assistant for Bangladeshi farmers. ");

        if (context != null) {
            prompt.append("The farmer is ").append(context.getFarmerName())
                    .append(" from ").append(context.getLocation()).append(". ");

            if (!context.getCurrentCrops().isEmpty()) {
                prompt.append("They grow: ").append(String.join(", ", context.getCurrentCrops())).append(". ");
            }
        }

        prompt.append("Answer this question in 2-3 sentences: ").append(message);
        return prompt.toString();
    }

    private String getGeminiErrorAdvice(String errorMessage) {
        if (errorMessage.contains("API key")) {
            return "Check your Gemini API key. Get a free one from https://makersuite.google.com/app/apikey";
        } else if (errorMessage.contains("quota") || errorMessage.contains("rate limit")) {
            return "You've hit the free rate limit. Wait a minute and try again. Free tier allows 15 requests/minute";
        } else if (errorMessage.contains("timeout")) {
            return "Request timed out. Gemini API might be slow. Try again with a shorter prompt";
        } else if (errorMessage.contains("model")) {
            return "Model not found. Make sure you're using 'gemini-1.5-flash' for free tier";
        } else {
            return "Check your internet connection and API key. Gemini API is completely free to use";
        }
    }

    private String getErrorRecommendation(String errorMessage) {
        if (errorMessage.contains("translation")) {
            return "Translation issue - check internet connection. Free translation services might be temporarily down";
        } else if (errorMessage.contains("Gemini") || errorMessage.contains("API")) {
            return "Gemini API issue - verify your free API key and check rate limits (15/minute)";
        } else if (errorMessage.contains("context")) {
            return "Farmer context issue - make sure user profile exists in database";
        } else {
            return "General error - check logs and verify all free services are accessible";
        }
    }
}
