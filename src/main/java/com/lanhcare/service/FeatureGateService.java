package com.lanhcare.service;

import com.lanhcare.dto.subscription.FeatureQuotaResponse;

/**
 * Service for checking and enforcing feature access based on user subscription.
 */
public interface FeatureGateService {

    /**
     * Check if user can access a feature. For quota-limited features,
     * also checks daily usage count.
     *
     * @param accountId The user's account ID
     * @param featureCode The feature code to check
     * @return true if user can access the feature
     */
    boolean canAccess(Integer accountId, String featureCode);

    /**
     * Record usage of a quota-limited feature (MEAL_LOG, EXERCISE_LOG, AI_CHAT).
     * Should be called AFTER successful execution of the feature.
     *
     * @param accountId The user's account ID
     * @param featureCode The feature code
     */
    void recordUsage(Integer accountId, String featureCode);

    /**
     * Get current quota info for a feature.
     *
     * @param accountId The user's account ID
     * @param featureCode The feature code
     * @return Quota info with used/limit/remaining
     */
    FeatureQuotaResponse getQuota(Integer accountId, String featureCode);
}
