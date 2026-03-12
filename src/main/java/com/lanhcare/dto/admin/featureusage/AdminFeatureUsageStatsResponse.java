package com.lanhcare.dto.admin.featureusage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Admin Feature Usage Analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFeatureUsageStatsResponse {
    
    private List<FeatureStats> featureStats;
    private List<FeatureStats> uniqueUserStats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeatureStats {
        private String featureCode;
        private Long totalUsage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyTrend {
        private String date;
        private Long usage;
    }
}
