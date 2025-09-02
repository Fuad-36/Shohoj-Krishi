package com.example.backend.service.chatbot;

import com.example.backend.dto.chatbot.request.ChatType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class ChatIntentClassifier {

    public ChatType detectChatType(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ChatType.GENERAL_FARMING;
        }

        String lowerMessage = message.toLowerCase().trim();

        // Market Price Detection
        if (containsAny(lowerMessage, "দাম", "price", "বাজার", "market", "বিক্রি", "sell", "টাকা")) {
            return ChatType.MARKET_PRICE;
        }

        // Fertilizer Advice
        if (containsAny(lowerMessage, "সার", "fertilizer", "ইউরিয়া", "urea", "পটাশ", "potash")) {
            return ChatType.FERTILIZER_ADVICE;
        }

        // Pest/Disease
        if (containsAny(lowerMessage, "রোগ", "disease", "পোকা", "pest", "চিকিৎসা", "treatment", "মাকড়", "fungus")) {
            return ChatType.PEST_DISEASE;
        }

        // Weather
        if (containsAny(lowerMessage, "আবহাওয়া", "weather", "বৃষ্টি", "rain", "গরম", "hot", "ঠান্ডা", "cold")) {
            return ChatType.WEATHER_ADVICE;
        }

        // Loan/Government
        if (containsAny(lowerMessage, "ঋণ", "loan", "সরকার", "government", "ভর্তুকি", "subsidy", "স্কিম", "scheme")) {
            return containsAny(lowerMessage, "ঋণ", "loan") ? ChatType.LOAN_ADVICE : ChatType.GOVERNMENT_SCHEMES;
        }

        // Harvest Timing
        if (containsAny(lowerMessage, "কাটা", "harvest", "সময়", "time", "কবে", "when")) {
            return ChatType.HARVEST_TIMING;
        }

        // Crop Advice (general farming terms)
        if (containsAny(lowerMessage, "চাষ", "farming", "ফসল", "crop", "বীজ", "seed", "জমি", "land")) {
            return ChatType.CROP_ADVICE;
        }

        return ChatType.GENERAL_FARMING;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
