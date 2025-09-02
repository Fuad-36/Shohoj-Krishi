package com.example.backend.service.chatbot;

import com.example.backend.dto.chatbot.response.FarmerContext;
import com.example.backend.dto.chatbot.response.PostInfo;
import com.example.backend.entity.posts.PostStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FarmerDataService {

    private final EntityManager entityManager;

    public Optional<FarmerContext> getFarmerContext(Long userId) {
        try {
            // Check if farmer profile exists
            String profileQuery = """
            SELECT fp.fullName, fp.division, fp.district, fp.upazila, fp.unionPorishod,
                   fp.farmSizeAc, fp.farmType
            FROM FarmerProfile fp WHERE fp.userId = :userId
            """;

            List<Object[]> profileResults = entityManager.createQuery(profileQuery, Object[].class)
                    .setParameter("userId", userId)
                    .getResultList();

            if (profileResults.isEmpty()) {
                log.warn("No FarmerProfile found for userId={}", userId);
                return Optional.empty();
            }

            Object[] profileResult = profileResults.get(0);

            // Check recent posts
            String postsQuery = """
            SELECT fp.cropName, fp.quantity, fp.quantityUnit, 
                   fp.pricePerUnit, fp.harvestDate, fp.status
            FROM FarmerPost fp WHERE fp.user.id = :userId 
            AND fp.createdAt > :cutoffDate
            ORDER BY fp.createdAt DESC
            """;

            List<Object[]> postsResults = entityManager.createQuery(postsQuery, Object[].class)
                    .setParameter("userId", userId)
                    .setParameter("cutoffDate", Instant.now().minus(90, ChronoUnit.DAYS)) // Last 3 months
                    .setMaxResults(10)
                    .getResultList();

            if (postsResults.isEmpty()) {
                log.info("FarmerProfile found for userId={}, but no recent FarmerPost in last 90 days", userId);
            }

            // Build context
            String fullName = profileResult[0] != null ? (String) profileResult[0] : "Unknown";
            String division = profileResult[1] != null ? (String) profileResult[1] : "Unknown Division";
            String district = profileResult[2] != null ? (String) profileResult[2] : "Unknown District";
            String upazila = profileResult[3] != null ? (String) profileResult[3] : "Unknown Upazila";
            String unionPorishod = profileResult[4] != null ? (String) profileResult[4] : "Unknown Union";
            BigDecimal farmSize = profileResult[5] != null ? (BigDecimal) profileResult[5] : BigDecimal.ZERO;
            String farmType = profileResult[6] != null ? (String) profileResult[6] : "general farming";

            String location = String.format("%s, %s, %s, %s", unionPorishod, upazila, district, division);

            List<PostInfo> recentPosts = postsResults.stream()
                    .map(row -> {
                        try {
                            return PostInfo.builder()
                                    .cropName(row[0] != null ? (String) row[0] : "Unknown Crop")
                                    .quantity(row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO)
                                    .quantityUnit(row[2] != null ? (String) row[2] : "unit")
                                    .pricePerUnit(row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO)
                                    .harvestDate(row[4] != null ? (LocalDate) row[4] : LocalDate.now())
                                    .status(String.valueOf(row[5] != null ? (PostStatus) row[5] : PostStatus.AVAILABLE))
                                    .build();
                        } catch (Exception ex) {
                            log.error("Error mapping FarmerPost row for userId={} → row={} → {}", userId, row, ex.getMessage());
                            return null;
                        }
                    })
                    .filter(p -> p != null)
                    .collect(Collectors.toList());

            List<String> currentCrops = recentPosts.stream()
                    .map(PostInfo::getCropName)
                    .distinct()
                    .collect(Collectors.toList());

            String expertise = determineExpertise(recentPosts, farmType);

            return Optional.of(FarmerContext.builder()
                    .farmerName(fullName)
                    .location(location)
                    .farmSize(farmSize)
                    .farmType(farmType)
                    .currentCrops(currentCrops)
                    .recentPosts(recentPosts)
                    .expertise(expertise)
                    .build());

        } catch (Exception e) {
            log.error("Unexpected error fetching farmer context for userId={}: {}", userId, e.getMessage(), e);
            return Optional.empty();
        }
    }


    private String determineExpertise(List<PostInfo> posts, String farmType) {
        if (posts.isEmpty()) {
            return farmType != null ? farmType : "general farming";
        }

        Map<String, Long> cropCounts = posts.stream()
                .collect(Collectors.groupingBy(PostInfo::getCropName, Collectors.counting()));

        String mostFrequentCrop = cropCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("mixed crops");

        return mostFrequentCrop + " cultivation";
    }
}
