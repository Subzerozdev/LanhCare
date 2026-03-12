package com.lanhcare.repository;

import com.lanhcare.entity.Subscription;
import com.lanhcare.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Subscription entity
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    /**
     * Find active subscription for an account
     */
    Optional<Subscription> findByAccountIdAndStatus(Integer accountId, SubscriptionStatus status);

    /**
     * Find all subscriptions for an account, ordered by start date descending
     */
    List<Subscription> findByAccountIdOrderByStartDateDesc(Integer accountId);

    /**
     * Check if account has an active subscription
     */
    boolean existsByAccountIdAndStatus(Integer accountId, SubscriptionStatus status);

    /**
     * Find subscription by transaction ID (for polling after payment)
     */
    Optional<Subscription> findByTransactionId(Integer transactionId);
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Count subscriptions by status
     */
    long countByStatus(SubscriptionStatus status);
    
    /**
     * Count subscriptions expiring within a date range
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE' " +
           "AND s.endDate BETWEEN :now AND :expiryDate")
    long countExpiringSoon(@Param("now") LocalDateTime now, 
                           @Param("expiryDate") LocalDateTime expiryDate);
    
    /**
     * Find subscriptions expiring soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.status = 'ACTIVE' " +
           "AND s.endDate BETWEEN :now AND :expiryDate " +
           "ORDER BY s.endDate ASC")
    Page<Subscription> findExpiringSoon(@Param("now") LocalDateTime now,
                                        @Param("expiryDate") LocalDateTime expiryDate,
                                        Pageable pageable);
    
    /**
     * Find all subscriptions with pagination
     */
    Page<Subscription> findAllByOrderByStartDateDesc(Pageable pageable);
    
    /**
     * Find subscriptions by status with pagination
     */
    Page<Subscription> findByStatusOrderByStartDateDesc(SubscriptionStatus status, Pageable pageable);
    
    /**
     * Find subscriptions by account ID with pagination
     */
    Page<Subscription> findByAccountIdOrderByStartDateDesc(Integer accountId, Pageable pageable);
    
    /**
     * Find subscriptions by status and account
     */
    Page<Subscription> findByStatusAndAccountIdOrderByStartDateDesc(
            SubscriptionStatus status, Integer accountId, Pageable pageable);
    
    /**
     * Count subscriptions by service plan
     */
    long countByServicePlanId(Integer servicePlanId);

    /**
     * Get subscription statistics by plan
     */
    @Query("SELECT s.servicePlan.id, s.servicePlan.name, COUNT(s) " +
           "FROM Subscription s WHERE s.status = :status " +
           "GROUP BY s.servicePlan.id, s.servicePlan.name " +
           "ORDER BY COUNT(s) DESC")
    List<Object[]> countByStatusGroupByServicePlan(@Param("status") SubscriptionStatus status);
}
