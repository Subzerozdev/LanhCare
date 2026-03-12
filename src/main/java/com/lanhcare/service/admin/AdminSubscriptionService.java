package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.subscription.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.Subscription;
import com.lanhcare.enums.SubscriptionStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.SubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Subscription Management Service
 */
@Service
@Transactional
public class AdminSubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }
    
    /**
     * Get all subscriptions with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminSubscriptionResponse> getAllSubscriptions(
            SubscriptionStatus status, Integer userId, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Subscription> subscriptionPage;
        
        if (status != null && userId != null) {
            subscriptionPage = subscriptionRepository.findByStatusAndAccountIdOrderByStartDateDesc(
                    status, userId, pageable);
        } else if (status != null) {
            subscriptionPage = subscriptionRepository.findByStatusOrderByStartDateDesc(status, pageable);
        } else if (userId != null) {
            subscriptionPage = subscriptionRepository.findByAccountIdOrderByStartDateDesc(userId, pageable);
        } else {
            subscriptionPage = subscriptionRepository.findAllByOrderByStartDateDesc(pageable);
        }
        
        List<AdminSubscriptionResponse> subscriptions = subscriptionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminSubscriptionResponse>builder()
                .content(subscriptions)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(subscriptionPage.getNumber())
                        .pageSize(subscriptionPage.getSize())
                        .totalElements(subscriptionPage.getTotalElements())
                        .totalPages(subscriptionPage.getTotalPages())
                        .first(subscriptionPage.isFirst())
                        .last(subscriptionPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get subscription detail
     */
    @Transactional(readOnly = true)
    public AdminSubscriptionDetailResponse getSubscriptionDetail(Integer id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));
        return mapToDetailResponse(subscription);
    }
    
    /**
     * Update subscription status
     */
    public AdminSubscriptionResponse updateSubscriptionStatus(Integer id, SubscriptionStatus newStatus) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));
        
        subscription.setStatus(newStatus);
        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponse(updated);
    }
    
    /**
     * Extend subscription by days
     */
    public AdminSubscriptionResponse extendSubscription(Integer id, int days) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + id));
        
        // Extend end date
        LocalDateTime newEndDate = subscription.getEndDate().plusDays(days);
        subscription.setEndDate(newEndDate);
        
        // If subscription was expired, reactivate it
        if (subscription.getStatus() == SubscriptionStatus.EXPIRED) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        
        Subscription updated = subscriptionRepository.save(subscription);
        return mapToResponse(updated);
    }
    
    /**
     * Get expiring subscriptions (within N days)
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminSubscriptionResponse> getExpiringSubscriptions(int days, int page, int size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(days);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Subscription> subscriptionPage = subscriptionRepository.findExpiringSoon(now, expiryDate, pageable);
        
        List<AdminSubscriptionResponse> subscriptions = subscriptionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminSubscriptionResponse>builder()
                .content(subscriptions)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(subscriptionPage.getNumber())
                        .pageSize(subscriptionPage.getSize())
                        .totalElements(subscriptionPage.getTotalElements())
                        .totalPages(subscriptionPage.getTotalPages())
                        .first(subscriptionPage.isFirst())
                        .last(subscriptionPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get subscription statistics
     */
    @Transactional(readOnly = true)
    public AdminSubscriptionStatsResponse getSubscriptionStats() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysFromNow = now.plusDays(7);
        
        long total = subscriptionRepository.count();
        long active = subscriptionRepository.countByStatus(SubscriptionStatus.ACTIVE);
        long expired = subscriptionRepository.countByStatus(SubscriptionStatus.EXPIRED);
        long cancelled = subscriptionRepository.countByStatus(SubscriptionStatus.CANCELLED);
        long pending = subscriptionRepository.countByStatus(SubscriptionStatus.PENDING);
        long expiringSoon = subscriptionRepository.countExpiringSoon(now, sevenDaysFromNow);
        
        // Stats by plan
        List<Object[]> planStats = subscriptionRepository.countByStatusGroupByServicePlan(SubscriptionStatus.ACTIVE);
        List<AdminSubscriptionStatsResponse.PlanSubscriptionCount> byPlan = planStats.stream()
                .map(row -> AdminSubscriptionStatsResponse.PlanSubscriptionCount.builder()
                        .servicePlanId((Integer) row[0])
                        .servicePlanName((String) row[1])
                        .activeCount(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());
        
        return AdminSubscriptionStatsResponse.builder()
                .totalSubscriptions(total)
                .activeSubscriptions(active)
                .expiredSubscriptions(expired)
                .cancelledSubscriptions(cancelled)
                .pendingSubscriptions(pending)
                .expiringSoon7Days(expiringSoon)
                .subscriptionsByPlan(byPlan)
                .build();
    }
    
    // ========== Helper Methods ==========
    
    private AdminSubscriptionResponse mapToResponse(Subscription subscription) {
        return AdminSubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getAccount().getId())
                .userName(subscription.getAccount().getFullname())
                .userEmail(subscription.getAccount().getEmail())
                .servicePlanId(subscription.getServicePlan().getId())
                .servicePlanName(subscription.getServicePlan().getName())
                .servicePlanPrice(subscription.getServicePlan().getPrice())
                .startDate(subscription.getStartDate().format(DATE_FORMATTER))
                .endDate(subscription.getEndDate().format(DATE_FORMATTER))
                .status(subscription.getStatus().toString())
                .transactionId(subscription.getTransaction() != null 
                        ? subscription.getTransaction().getId() : null)
                .build();
    }
    
    private AdminSubscriptionDetailResponse mapToDetailResponse(Subscription subscription) {
        AdminSubscriptionDetailResponse.TransactionInfo txnInfo = null;
        if (subscription.getTransaction() != null) {
            txnInfo = AdminSubscriptionDetailResponse.TransactionInfo.builder()
                    .id(subscription.getTransaction().getId())
                    .amount(subscription.getTransaction().getAmount())
                    .paymentMethod(subscription.getTransaction().getPaymentMethod())
                    .status(subscription.getTransaction().getStatus().toString())
                    .transactionDate(subscription.getTransaction().getTransactionDate() != null 
                            ? subscription.getTransaction().getTransactionDate().format(DATE_FORMATTER) : null)
                    .build();
        }
        
        return AdminSubscriptionDetailResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getAccount().getId())
                .userName(subscription.getAccount().getFullname())
                .userEmail(subscription.getAccount().getEmail())
                .servicePlanId(subscription.getServicePlan().getId())
                .servicePlanName(subscription.getServicePlan().getName())
                .servicePlanPrice(subscription.getServicePlan().getPrice())
                .startDate(subscription.getStartDate().format(DATE_FORMATTER))
                .endDate(subscription.getEndDate().format(DATE_FORMATTER))
                .status(subscription.getStatus().toString())
                .transaction(txnInfo)
                .build();
    }
}
