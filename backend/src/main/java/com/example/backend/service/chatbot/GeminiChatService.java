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
            // Translate Bengali to English for better AI understanding
            String englishMessage = translationService.translateToEnglish(request.getMessage());
            String personalizedPrompt = buildPersonalizedPrompt(englishMessage, detectedChatType, context);
            String response = callGeminiAPI(personalizedPrompt);

            // If response language is "bn", translate back to Bengali
            String finalResponse = response;
            if ("bn".equals(request.getLanguage())) {
                finalResponse = translationService.translateToBengali(response);
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
            log.error("Error generating contextual response: {}", e.getMessage());
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
            if ("bn".equals(request.getLanguage())) {
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
            log.error("Error generating general response: {}", e.getMessage());
            return createErrorResponse(detectedChatType, request.getLanguage());
        }
    }

    private String buildPersonalizedPrompt(String userMessage, ChatType chatType, FarmerContext context) {
        StringBuilder prompt = new StringBuilder();

        // System instructions for Gemini
        prompt.append("You are Shohoj Krishok, an expert agricultural assistant for Bangladeshi farmers. ");
        prompt.append("You provide practical, actionable advice in simple language. ");
        prompt.append("Always be supportive, encouraging, and focus on solutions. ");

        // Add farmer-specific context
        prompt.append("\nFarmer Profile:\n");
        prompt.append("Name: ").append(context.getFarmerName()).append("\n");
        prompt.append("Location: ").append(context.getLocation()).append("\n");

        if (context.getFarmSize() != null) {
            prompt.append("Farm Size: ").append(context.getFarmSize()).append(" acres\n");
        }

        if (context.getFarmType() != null) {
            prompt.append("Farm Type: ").append(context.getFarmType()).append("\n");
        }

        if (!context.getCurrentCrops().isEmpty()) {
            prompt.append("Current Crops: ").append(String.join(", ", context.getCurrentCrops())).append("\n");
        }

        // Add recent posts context
        if (!context.getRecentPosts().isEmpty()) {
            prompt.append("Recent Harvest/Sales:\n");
            context.getRecentPosts().stream()
                    .limit(3)
                    .forEach(post -> {
                        prompt.append("- ").append(post.getCropName())
                                .append(": ").append(post.getQuantity()).append(" ").append(post.getQuantityUnit())
                                .append(" at ").append(post.getPricePerUnit()).append(" per unit")
                                .append(" (").append(post.getHarvestDate()).append(")\n");
                    });
        }

        // Add chat type specific instructions
        prompt.append("\nTask: ");
        switch (chatType) {
            case CROP_ADVICE:
                prompt.append("Provide specific crop cultivation advice considering their location, current crops, and farming experience.");
                break;

            case MARKET_PRICE:
                prompt.append("Provide current market price information and selling strategies for their crops and location.");
                break;

            case FERTILIZER_ADVICE:
                prompt.append("Recommend appropriate fertilizers for their specific crops, farm size, and local soil conditions.");
                break;

            case WEATHER_ADVICE:
                prompt.append("Give weather-related farming advice specific to ").append(context.getLocation()).append(" region.");
                break;

            case PEST_DISEASE:
                prompt.append("Help identify and provide treatment solutions for pest/disease issues affecting their crops.");
                break;

            case SELLING_ADVICE:
                prompt.append("Provide marketing and selling strategies based on their crops and recent harvest data.");
                break;

            case GOVERNMENT_SCHEMES:
                prompt.append("Inform about relevant government agricultural schemes and subsidies for their area and farm size.");
                break;

            case LOAN_ADVICE:
                prompt.append("Provide agricultural loan guidance suitable for their farm size and farming activities.");
                break;

            default:
                prompt.append("Provide helpful farming advice considering their specific situation.");
                break;
        }

        prompt.append("\n\nGuidelines:\n");
        prompt.append("- Keep response practical and actionable\n");
        prompt.append("- Use simple, clear language\n");
        prompt.append("- Provide specific steps when possible\n");
        prompt.append("- Consider Bangladesh's climate and farming conditions\n");
        prompt.append("- Be encouraging and supportive\n");
        prompt.append("- Limit response to 150 words\n");

        prompt.append("\nFarmer's Question: ").append(userMessage);
        prompt.append("\n\nYour Response:");

        return prompt.toString();
    }

    private String buildGeneralPrompt(String userMessage, ChatType chatType) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are Shohoj Krishok, an expert agricultural assistant for Bangladeshi farmers. ");
        prompt.append("You provide practical, actionable advice in simple language. ");
        prompt.append("Always be supportive, encouraging, and focus on solutions. ");

        prompt.append("\nTask: ");
        switch (chatType) {
            case CROP_ADVICE:
                prompt.append("Provide general crop cultivation advice for Bangladeshi farmers.");
                break;
            case MARKET_PRICE:
                prompt.append("Provide general market price guidance and selling tips for Bangladesh's agricultural markets.");
                break;
            case FERTILIZER_ADVICE:
                prompt.append("Recommend fertilizers suitable for common crops in Bangladesh.");
                break;
            case WEATHER_ADVICE:
                prompt.append("Give general weather-related farming advice for Bangladesh's climate.");
                break;
            default:
                prompt.append("Provide helpful general farming advice for Bangladeshi farmers.");
                break;
        }

        prompt.append("\n\nGuidelines:\n");
        prompt.append("- Keep response practical and actionable\n");
        prompt.append("- Use simple, clear language\n");
        prompt.append("- Consider Bangladesh's climate and farming conditions\n");
        prompt.append("- Be encouraging and supportive\n");
        prompt.append("- Limit response must be under 100 characters\n");

        prompt.append("\nFarmer's Question: ").append(userMessage);
        prompt.append("\n\nYour Response:");

        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) {
        try {
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
                            .maxOutputTokens(config.getMaxTokens())
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

            ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(uri, entity, GeminiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractTextFromGeminiResponse(response.getBody());
            }

            log.error("Gemini API call failed with status: {}", response.getStatusCode());
            return "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না। দয়া করে কিছুক্ষণ পরে আবার চেষ্টা করুন।";

        } catch (Exception e) {
            log.error("Gemini API call failed: {}", e.getMessage());
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
            if (candidate.getContent() != null && candidate.getContent().getParts() != null &&
                    !candidate.getContent().getParts().isEmpty()) {
                return candidate.getContent().getParts().get(0).getText().trim();
            }
        }
        return "আমি এই মুহূর্তে আপনার প্রশ্নের উত্তর দিতে পারছি না।";
    }

    private List<String> generateRelatedSuggestions(FarmerContext context, ChatType chatType) {
        List<String> suggestions = new ArrayList<>();

        // Generate suggestions based on farmer's crops and context
        if (!context.getCurrentCrops().isEmpty()) {
            String mainCrop = context.getCurrentCrops().get(0);
            suggestions.add(mainCrop + " এর বাজার দাম কেমন?");
            suggestions.add(mainCrop + " এর জন্য কোন সার ভালো?");
            suggestions.add(mainCrop + " এর রোগ-বালাই প্রতিরোধ কিভাবে করবো?");
        }

        // Add location-specific suggestions
        suggestions.add(context.getLocation() + " এলাকার আবহাওয়া পরামর্শ চাই");

        // Add farm size specific suggestions
        if (context.getFarmSize() != null) {
            suggestions.add(context.getFarmSize() + " একর জমির জন্য ঋণ পরামর্শ চাই");
        }

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
}
