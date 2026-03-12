package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.dashboard.AdminDashboardResponse;
import com.lanhcare.entity.Transaction;
import com.lanhcare.enums.*;
import com.lanhcare.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Dashboard Service
 * Provides overview statistics for admin panel
 */
@Service
@Transactional(readOnly = true)
public class AdminDashboardService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminDashboardService(AccountRepository accountRepository,
                                  TransactionRepository transactionRepository,
                                  SubscriptionRepository subscriptionRepository,
                                  PostRepository postRepository,
                                  CommentRepository commentRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }
    
    /**
     * Get dashboard overview
     */
    public AdminDashboardResponse getDashboardOverview() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfToday = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime sevenDaysFromNow = now.plusDays(7);
        
        // User stats
        long totalUsers = accountRepository.count();
        long activeUsers = accountRepository.countByStatus(AccountStatus.ACTIVE);
        long newUsersToday = accountRepository.countByRole(AccountRole.USER); // Approximate
        long newUsersThisMonth = totalUsers; // Will be refined when createdAt field exists
        
        // Revenue stats
        BigDecimal totalRevenue = transactionRepository.calculateTotalRevenue();
        BigDecimal revenueThisMonth = transactionRepository.calculateRevenueByDateRange(startOfMonth, now);
        
        // Subscription stats
        long activeSubscriptions = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        long expiringSoon = subscriptionRepository.countExpiringSoon(now, sevenDaysFromNow);
        
        // Moderation stats
        long pendingPosts = postRepository.countByStatus(PostStatus.PENDING);
        long pendingComments = commentRepository.countByStatus(CommentStatus.PENDING);
        
        // Recent transactions (last 5)
        List<Transaction> recentTxns = transactionRepository.findAllByOrderByTransactionDateDesc(
                PageRequest.of(0, 5)).getContent();
        
        List<AdminDashboardResponse.RecentTransaction> recentTransactions = recentTxns.stream()
                .map(t -> AdminDashboardResponse.RecentTransaction.builder()
                        .id(t.getId())
                        .userName(t.getAccount().getFullname())
                        .userEmail(t.getAccount().getEmail())
                        .servicePlanName(t.getServicePlan().getName())
                        .amount(t.getAmount())
                        .status(t.getStatus().toString())
                        .transactionDate(t.getTransactionDate() != null 
                                ? t.getTransactionDate().format(DATE_FORMATTER) : null)
                        .build())
                .collect(Collectors.toList());
        
        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .newUsersToday(newUsersToday)
                .newUsersThisMonth(newUsersThisMonth)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .activeSubscriptions(activeSubscriptions)
                .expiringSoonSubscriptions(expiringSoon)
                .pendingPosts(pendingPosts)
                .pendingComments(pendingComments)
                .recentTransactions(recentTransactions)
                .build();
    }
}
