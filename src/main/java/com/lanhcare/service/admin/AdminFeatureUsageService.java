package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.featureusage.AdminFeatureUsageStatsResponse;
import com.lanhcare.repository.FeatureUsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Feature Usage Analytics Service
 */
@Service
@Transactional(readOnly = true)
public class AdminFeatureUsageService {
    
    private final FeatureUsageRepository featureUsageRepository;
    
    public AdminFeatureUsageService(FeatureUsageRepository featureUsageRepository) {
        this.featureUsageRepository = featureUsageRepository;
    }
    
    /**
     * Get overall feature usage statistics
     */
    public AdminFeatureUsageStatsResponse getOverallStats(LocalDate startDate, LocalDate endDate) {
        // Get total usage by feature
        List<Object[]> usageData;
        if (startDate != null && endDate != null) {
            usageData = featureUsageRepository.getUsageByFeatureAndDateRange(startDate, endDate);
        } else {
            usageData = featureUsageRepository.getTotalUsageByFeature();
        }
        
        List<AdminFeatureUsageStatsResponse.FeatureStats> featureStats = usageData.stream()
                .map(row -> AdminFeatureUsageStatsResponse.FeatureStats.builder()
                        .featureCode((String) row[0])
                        .totalUsage(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        // Get unique users per feature
        List<Object[]> uniqueUsersData = featureUsageRepository.getUniqueUsersByFeature();
        List<AdminFeatureUsageStatsResponse.FeatureStats> uniqueUserStats = uniqueUsersData.stream()
                .map(row -> AdminFeatureUsageStatsResponse.FeatureStats.builder()
                        .featureCode((String) row[0])
                        .totalUsage(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        return AdminFeatureUsageStatsResponse.builder()
                .featureStats(featureStats)
                .uniqueUserStats(uniqueUserStats)
                .build();
    }
    
    /**
     * Get daily usage trend for a specific feature
     */
    public List<AdminFeatureUsageStatsResponse.DailyTrend> getFeatureTrend(
            String featureCode, LocalDate startDate, LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        
        List<Object[]> trendData = featureUsageRepository.getDailyTrend(featureCode, startDate, endDate);
        
        return trendData.stream()
                .map(row -> AdminFeatureUsageStatsResponse.DailyTrend.builder()
                        .date(row[0].toString())
                        .usage(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }
}
