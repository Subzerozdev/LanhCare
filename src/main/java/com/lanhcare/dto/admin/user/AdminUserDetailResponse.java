package com.lanhcare.dto.admin.user;

import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for detailed User response (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDetailResponse {
    
    private Integer id;
    private String email;
    private String fullname;
    private AccountRole role;
    private AccountStatus status;
    
    // Health Profile Summary
    private HealthProfileSummary healthProfile;
    
    // Transaction Summary
    private List<TransactionSummary> recentTransactions;
    private Integer totalTransactionCount;
    private BigDecimal totalSpent;
    
    // Meal Log Count
    private Integer mealLogCount;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HealthProfileSummary {
        private Double heightCm;
        private BigDecimal currentWeightKg;
        private Double bmi;
        private String activityLevel;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionSummary {
        private Integer id;
        private BigDecimal amount;
        private String status;
        private String transactionDate;
    }
}
