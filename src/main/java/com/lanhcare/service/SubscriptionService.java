package com.lanhcare.service;

import com.lanhcare.dto.subscription.PurchaseResponse;
import com.lanhcare.dto.subscription.PurchaseSubscriptionRequest;
import com.lanhcare.dto.subscription.SubscriptionResponse;
import com.lanhcare.dto.subscription.TransactionHistoryResponse;
import com.lanhcare.dto.subscription.TransactionStatusResponse;

import java.util.List;
import java.util.Map;

/**
 * Subscription Service Interface
 */
public interface SubscriptionService {

    /**
     * Get the current active subscription of the authenticated user
     */
    SubscriptionResponse getMySubscription(Integer accountId);

    /**
     * Purchase a subscription plan
     */
    PurchaseResponse purchaseSubscription(Integer accountId, PurchaseSubscriptionRequest request, String ipAddress);

    /**
     * Cancel the current active subscription
     */
    void cancelSubscription(Integer accountId);

    /**
     * Get transaction history for the authenticated user
     */
    List<TransactionHistoryResponse> getMyTransactions(Integer accountId);

    /**
     * Process VNPay payment callback
     */
    void processVNPayCallback(Map<String, String> params);

    /**
     * Check if user has a specific feature based on their subscription
     */
    boolean hasFeature(Integer accountId, String featureCode);

    /**
     * Get transaction status for mobile polling after VNPay payment
     */
    TransactionStatusResponse getTransactionStatus(Integer transactionId, Integer accountId);
}
