package com.example.backend.dto.chatbot.request;

public enum ChatType {
    CROP_ADVICE,        // Advice about specific crops farmer grows
    MARKET_PRICE,       // Prices for farmer's crops in their area
    WEATHER_ADVICE,     // Weather-specific advice for farmer's location
    FERTILIZER_ADVICE,  // Fertilizer advice for farmer's crops
    PEST_DISEASE,       // Disease advice for farmer's crops
    HARVEST_TIMING,     // When to harvest based on farmer's posts
    SELLING_ADVICE,     // How to sell based on farmer's posts
    GENERAL_FARMING,    // General farming questions
    GOVERNMENT_SCHEMES, // Government schemes for farmer's area
    LOAN_ADVICE        // Loan advice based on farmer's farm size
}
