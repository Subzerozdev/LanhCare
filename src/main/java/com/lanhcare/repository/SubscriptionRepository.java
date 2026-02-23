package com.lanhcare.repository;

import com.lanhcare.entity.Subscription;
import com.lanhcare.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
