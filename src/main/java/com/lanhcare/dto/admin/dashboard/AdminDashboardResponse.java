package com.lanhcare.dto.admin.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Admin Dashboard Overview
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    
    // User stats
    private Long totalUsers;
    private Long activeUsers;
    private Long newUsersToday;
    private Long newUsersThisMonth;
    
    // Revenue stats
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisMonth;
    
    // Subscription stats
    private Long activeSubscriptions;
    private Long expiringSoonSubscriptions;
    
    // Moderation stats
    private Long pendingPosts;
    private Long pendingComments;
    
    // Recent activity
    private List<RecentTransaction> recentTransactions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentTransaction {
        private Integer id;
        private String userName;
        private String userEmail;
        private String servicePlanName;
        private BigDecimal amount;
        private String status;
        private String transactionDate;
    }
}
