package com.example.backend.service.chatbot;

import com.example.backend.config.ChatbotConfig;
import com.example.backend.dto.chatbot.request.*;
import com.example.backend.dto.chatbot.response.Candidate;
import com.example.backend.dto.chatbot.response.FarmerContext;
import com.example.backend.dto.chatbot.response.GeminiResponse;
import com.example.backend.dto.chatbot.response.PersonalizedChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiChatService {

    private final ChatbotConfig config;
    private final TranslationService translationService;
    private final FarmerDataService farmerDataService;
    private final RestTemplate restTemplate;
    private final ChatIntentClassifier chatIntentClassifier;

    public PersonalizedChatResponse generatePersonalizedResponse(PersonalizedChatRequest request) {

        if (request.isIncludePersonalContext()) {
            // Get farmer context first
            Optional<FarmerContext> farmerContext = farmerDataService.getFarmerContext(request.getUserId());

            if (farmerContext.isPresent()) {
                return generateContextualResponse(request, farmerContext.get());
            }
        }

        // Fallback to general response
        return generateGeneralResponse(request);
    }

    private PersonalizedChatResponse generateContextualResponse(PersonalizedChatRequest request, FarmerContext context) {
        ChatType detectedChatType = chatIntentClassifier.detectChatType(request.getMessage());
        try {
            // Only translate user message if it's in Bengali and translation is working properly
            String englishMessage = request.getMessage();
            if (containsBengaliScript(request.getMessage())) {
                String translated = translationService.translateToEnglish(request.getMessage());
                log.info("Original message: {}", request.getMessage());
                log.info("Translated message: {}", translated);

                // Check if translation was successful (not URL encoded garbage)
                if (translated != null && !translated.contains("%") && !translated.equals(request.getMessage())) {
                    englishMessage = translated;
                    log.info("Using translated message for prompt");
                } else {
                    log.warn("Translation failed or returned garbage, using original message");
                    // Use original message and let Gemini handle Bengali directly
                    englishMessage = request.getMessage();
                }
            }

            String personalizedPrompt = buildPersonalizedPrompt(englishMessage, detectedChatType, context);
            log.info("Prompt length: {} characters", personalizedPrompt.length());

            String response = callGeminiAPI(personalizedPrompt);
            log.info("Gemini response: {}", response);

            // For Bengali requests, use response as-is since Gemini can respond in Bengali
            String finalResponse = response;

            // Only translate if response is in English and user wants Bengali
            if ("bn".equals(request.getLanguage()) && !containsBengaliScript(response)) {
                String translatedResponse = translationService.translateToBengali(response);
                // Check if translation was successful
                if (translatedResponse != null && !translatedResponse.contains("QUERY LENGTH LIMIT EXCEEDED")
                        && !translatedResponse.equals(response)) {
                    finalResponse = translatedResponse;
                    log.info("Successfully translated response to Bengali");
                } else {
                    log.warn("Translation of response failed, using original response");
                    // Keep the original response
                }
            }

            return PersonalizedChatResponse.builder()
                    .response(finalResponse)
                    .originalLanguage(request.getLanguage())
                    .chatType(detectedChatType)
                    .farmerContext(context)
                    .relatedSuggestions(generateRelatedSuggestions(context, detectedChatType))
                    .metadata(Map.of("personalized", true, "farmerName", context.getFarmerName(), "model", "gemini"))
                    .build();

        } catch (Exception e) {
            log.error("Error generating contextual response: {}", e.getMessage(), e);
            return createErrorResponse(detectedChatType, request.getLanguage());
        }
    }

    private PersonalizedChatResponse generateGeneralResponse(PersonalizedChatRequest request) {
        ChatType detectedChatType = chatIntentClassifier.detectChatType(request.getMessage());
        try {
            String englishMessage = translationService.translateToEnglish(request.getMessage());
            String generalPrompt = buildGeneralPrompt(englishMessage, detectedChatType);
            String response = callGeminiAPI(generalPrompt);

            // If response language is "bn", translate back to Bengali
            String finalResponse = response;
            if ("bn".equals(request.getLanguage()) && !containsBengaliScript(response)) {
                finalResponse = translationService.translateToBengali(response);
            }

            return PersonalizedChatResponse.builder()
                    .response(finalResponse)
                    .originalLanguage(request.getLanguage())
                    .chatType(detectedChatType)
                    .relatedSuggestions(getGeneralSuggestions(detectedChatType))
                    .metadata(Map.of("personalized", false, "model", "gemini"))
                    .build();

        } catch (Exception e) {
            log.error("Error generating general response: {}", e.getMessage(), e);
            return createErrorResponse(detectedChatType, request.getLanguage());
        }
    }

    private String buildPersonalizedPrompt(String userMessage, ChatType chatType, FarmerContext context) {
        StringBuilder prompt = new StringBuilder();

        // Very concise system instructions
        prompt.append("You are Shohoj Krishok, Bangladesh farming assistant. Give practical advice in simple language. ");

        // If the question is in Bengali, respond in Bengali
        if (containsBengaliScript(userMessage)) {
            prompt.append("Respond in Bengali.\n\n");
        } else {
            prompt.append("Respond in English.\n\n");
        }

        // Minimal context - only essential info
        prompt.append("Farmer: ").append(context.getFarmerName())
                .append(", ").append(context.getLocation().split(",")[0]); // Just first part of location

        // Only include valid crops
        List<String> validCrops = context.getCurrentCrops().stream()
                .filter(crop -> !crop.equals("Unknown Crop") && !crop.equals("Weed"))
                .limit(2) // Limit to 2 crops to save space
                .collect(Collectors.toList());

        if (!validCrops.isEmpty()) {
            prompt.append(", Crops: ").append(String.join(", ", validCrops));
        }
        prompt.append("\n\n");

        // Very brief task description
        switch (chatType) {
            case PEST_DISEASE:
                prompt.append("Help with pest/disease in crops. ");
                break;
            case CROP_ADVICE:
                prompt.append("Give crop cultivation advice. ");
                break;
            case FERTILIZER_ADVICE:
                prompt.append("Recommend fertilizers. ");
                break;
            default:
                prompt.append("Give farming advice. ");
                break;
        }

        prompt.append("Keep answer under 100 words.\n\n");
        prompt.append("Q: ").append(userMessage).append("\nA:");

        return prompt.toString();
    }

    private String buildGeneralPrompt(String userMessage, ChatType chatType) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are Shohoj Krishok, Bangladesh farming assistant. Give practical advice in simple language. ");

        if (containsBengaliScript(userMessage)) {
            prompt.append("Respond in Bengali.\n\n");
        } else {
            prompt.append("Respond in English.\n\n");
        }

        switch (chatType) {
            case PEST_DISEASE:
                prompt.append("Help with pest/disease in crops. ");
                break;
            case CROP_ADVICE:
                prompt.append("Give crop cultivation advice. ");
                break;
            case FERTILIZER_ADVICE:
                prompt.append("Recommend fertilizers. ");
                break;
            default:
                prompt.append("Give farming advice. ");
                break;
        }

        prompt.append("Keep answer under 80 words.\n\n");
        prompt.append("Q: ").append(userMessage).append("\nA:");

        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) {
        try {
            log.info("Calling Gemini API with prompt length: {}", prompt.length());

            // Build Gemini request
            GeminiRequest request = GeminiRequest.builder()
                    .contents(List.of(
                            Content.builder()
                                    .parts(List.of(Part.builder().text(prompt).build()))
                                    .role("user")
                                    .build()
                    ))
                    .generationConfig(GenerationConfig.builder()
                            .temperature(config.getTemperature())
                            .maxOutputTokens(Math.min(config.getMaxTokens(), 4092)) // Limit output tokens to prevent MAX_TOKENS issue
                            .topP(0.8)
                            .topK(40)
                            .build())
                    .safetySettings(getSafetySettings())
                    .build();

            String uri = config.getGeminiBaseUrl() + "/" + config.getGeminiModel() +
                    ":generateContent?key=" + config.getGeminiApiKey();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

            log.info("Making request to Gemini API: {}", uri.replaceAll("key=.*", "key=***"));

            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(uri, entity, GeminiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String extractedText = extractTextFromGeminiResponse(response.getBody());
                log.info("Successfully extracted text from Gemini response");
                return extractedText;
            }

            log.error("Gemini API call failed with status: {}", response.getStatusCode());
            return "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না। দয়া করে কিছুক্ষণ পরে আবার চেষ্টা করুন।";

        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage(), e);
            return "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না। দয়া করে কিছুক্ষণ পরে আবার চেষ্টা করুন।";
        }
    }

    private List<SafetySetting> getSafetySettings() {
        List<String> categories = List.of(
                "HARM_CATEGORY_HARASSMENT",
                "HARM_CATEGORY_HATE_SPEECH",
                "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "HARM_CATEGORY_DANGEROUS_CONTENT"
        );

        String threshold = switch (config.getSafetyLevel()) {
            case 0 -> "BLOCK_NONE";
            case 1 -> "BLOCK_ONLY_HIGH";
            case 2 -> "BLOCK_MEDIUM_AND_ABOVE";
            case 3 -> "BLOCK_LOW_AND_ABOVE";
            default -> "BLOCK_MEDIUM_AND_ABOVE";
        };

        return categories.stream()
                .map(category -> SafetySetting.builder()
                        .category(category)
                        .threshold(threshold)
                        .build())
                .collect(Collectors.toList());
    }

    private String extractTextFromGeminiResponse(GeminiResponse response) {
        if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
            Candidate candidate = response.getCandidates().get(0);

            // Check if response was cut off due to MAX_TOKENS
            if ("MAX_TOKENS".equals(candidate.getFinishReason())) {
                log.warn("Gemini response was cut off due to MAX_TOKENS limit");
            }

            if (candidate.getContent() != null && candidate.getContent().getParts() != null &&
                    !candidate.getContent().getParts().isEmpty()) {
                String text = candidate.getContent().getParts().get(0).getText();
                return text != null ? text.trim() : "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না।";
            } else {
                log.warn("No content parts found in Gemini response, candidate: {}", candidate);
            }
        }
        log.warn("No valid content found in Gemini response: {}", response);
        return "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না।";
    }

    private List<String> generateRelatedSuggestions(FarmerContext context, ChatType chatType) {
        List<String> suggestions = new ArrayList<>();

        // Generate suggestions based on farmer's crops and context
        List<String> validCrops = context.getCurrentCrops().stream()
                .filter(crop -> !crop.equals("Unknown Crop") && !crop.equals("Weed"))
                .collect(Collectors.toList());

        if (!validCrops.isEmpty()) {
            String mainCrop = validCrops.get(0);
            suggestions.add(mainCrop + " এর বাজার দাম কেমন?");
            suggestions.add(mainCrop + " এর জন্য কোন সার ভালো?");
            suggestions.add(mainCrop + " এর রোগ-বালাই প্রতিরোধ কিভাবে করবো?");
        } else {
            // Default suggestions for rice (ধান) since that's what user is asking about
            suggestions.add("ধানের বাজার দাম কেমন?");
            suggestions.add("ধানের জন্য কোন সার ভালো?");
            suggestions.add("ধানের রোগ-বালাই প্রতিরোধ কিভাবে করবো?");
        }

        // Add a general suggestion
        suggestions.add("আবহাওয়া পরামর্শ চাই");

        return suggestions.stream().limit(4).collect(Collectors.toList());
    }

    private List<String> getGeneralSuggestions(ChatType chatType) {
        return List.of(
                "আজকের বাজার দাম জানতে চাই",
                "ভালো সার পেতে কোথায় যাবো?",
                "সরকারি ভর্তুকি কিভাবে পাবো?",
                "ফসলের রোগ-বালাই চিকিৎসা জানতে চাই"
        );
    }

    private PersonalizedChatResponse createErrorResponse(ChatType chatType, String language) {
        String errorMessage = "bn".equals(language) ?
                "দুঃখিত, আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না। দয়া করে কিছুক্ষণ পরে আবার চেষ্টা করুন।" :
                "Sorry, I cannot answer your question right now. Please try again later.";

        List<String> errorSuggestions = "bn".equals(language) ?
                List.of("পরে আবার চেষ্টা করুন", "অন্য প্রশ্ন করুন") :
                List.of("Try again later", "Ask another question");

        return PersonalizedChatResponse.builder()
                .response(errorMessage)
                .originalLanguage(language)
                .chatType(chatType)
                .relatedSuggestions(errorSuggestions)
                .metadata(Map.of("error", true, "model", "gemini"))
                .build();
    }

    private boolean containsBengaliScript(String text) {
        return text != null && text.matches(".*[\\u0980-\\u09FF].*");
    }

    public String generateTextFromPrompt(String prompt, String language) {
        try {
            String response = callGeminiAPI(prompt);

            if (response == null || response.isBlank()) {
                log.warn("Gemini returned empty response for prompt");
                return null;
            }

            // If the caller asked for Bengali and Gemini returned English, translate it
            if ("bn".equals(language) && !containsBengaliScript(response)) {
                try {
                    String translated = translationService.translateToBengali(response);
                    if (translated != null && !translated.isBlank() && !translated.contains("QUERY LENGTH LIMIT EXCEEDED")) {
                        return translated;
                    } else {
                        log.warn("TranslationService returned unusable translation, falling back to original Gemini response");
                    }
                } catch (Exception te) {
                    log.warn("Translation failed: {}", te.getMessage());
                }
            }
            return response;
        } catch (Exception e) {
            log.error("generateTextFromPrompt failed: {}", e.getMessage(), e);
            return null;
        }
    }
}