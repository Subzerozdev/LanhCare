package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.FeatureQuotaResponse;
import com.lanhcare.entity.FeatureUsage;
import com.lanhcare.repository.FeatureUsageRepository;
import com.lanhcare.service.FeatureGateService;
import com.lanhcare.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureGateServiceImpl implements FeatureGateService {

    private final SubscriptionService subscriptionService;
    private final FeatureUsageRepository featureUsageRepository;

    // Features that have daily quotas for Free users
    private static final Map<String, Integer> FREE_QUOTAS = Map.of(
            "MEAL_LOG", 1,
            "EXERCISE_LOG", 1,
            "AI_CHAT", 3
    );

    // Features that have daily quotas for Basic (Cơ bản) users
    private static final Map<String, Integer> BASIC_QUOTAS = Map.of(
            "AI_CHAT", 10
    );

    // Features that are always available for Free users (no quota needed)
    private static final Set<String> FREE_UNLIMITED = Set.of(
            "DAILY_LOG", "DAILY_LOG_LIMITED", "HOSPITAL_SEARCH", "FORUM_VIEW"
    );

    // Quota-limited feature codes
    private static final Set<String> QUOTA_FEATURES = Set.of(
            "MEAL_LOG", "EXERCISE_LOG", "AI_CHAT"
    );

    @Override
    @Transactional(readOnly = true)
    public boolean canAccess(Integer accountId, String featureCode) {
        // Normalize AI feature codes
        String normalizedCode = normalizeFeatureCode(featureCode);

        // 1. Check if user has the feature via subscription
        boolean hasFullFeature = subscriptionService.hasFeature(accountId, featureCode);

        if (hasFullFeature) {
            // User has the feature — check if it has a quota for their tier
            if (QUOTA_FEATURES.contains(normalizedCode)) {
                // Check if this is a limited tier (has AI_CHAT_LIMITED but not AI_CHAT_UNLIMITED)
                boolean isUnlimited = subscriptionService.hasFeature(accountId, normalizedCode + "_UNLIMITED")
                        || !subscriptionService.hasFeature(accountId, normalizedCode + "_LIMITED");

                // For MEAL_LOG and EXERCISE_LOG with paid subscription → unlimited
                if (!normalizedCode.equals("AI_CHAT")) {
                    return true; // Paid users have unlimited meal/exercise
                }

                if (isUnlimited) {
                    return true; // AI_CHAT_UNLIMITED
                }

                // AI_CHAT_LIMITED → check quota
                int limit = BASIC_QUOTAS.getOrDefault(normalizedCode, Integer.MAX_VALUE);
                int used = getTodayUsage(accountId, normalizedCode);
                return used < limit;
            }
            return true;
        }

        // 2. User does NOT have the feature — check Free tier quotas
        if (FREE_UNLIMITED.contains(featureCode)) {
            return true;
        }

        if (FREE_QUOTAS.containsKey(normalizedCode)) {
            int limit = FREE_QUOTAS.get(normalizedCode);
            int used = getTodayUsage(accountId, normalizedCode);
            return used < limit;
        }

        // Feature not available at all for Free users
        return false;
    }

    @Override
    @Transactional
    public void recordUsage(Integer accountId, String featureCode) {
        String normalizedCode = normalizeFeatureCode(featureCode);

        if (!QUOTA_FEATURES.contains(normalizedCode)) {
            return; // Not a quota feature, nothing to record
        }

        LocalDate today = LocalDate.now();
        FeatureUsage usage = featureUsageRepository
                .findByAccountIdAndFeatureCodeAndUsageDate(accountId, normalizedCode, today)
                .orElse(FeatureUsage.builder()
                        .accountId(accountId)
                        .featureCode(normalizedCode)
                        .usageDate(today)
                        .usageCount(0)
                        .build());

        usage.setUsageCount(usage.getUsageCount() + 1);
        featureUsageRepository.save(usage);

        log.debug("Recorded usage for account={}, feature={}, count={}",
                accountId, normalizedCode, usage.getUsageCount());
    }

    @Override
    @Transactional(readOnly = true)
    public FeatureQuotaResponse getQuota(Integer accountId, String featureCode) {
        String normalizedCode = normalizeFeatureCode(featureCode);
        int used = getTodayUsage(accountId, normalizedCode);
        int limit = getLimit(accountId, normalizedCode);

        return FeatureQuotaResponse.builder()
                .featureCode(normalizedCode)
                .used(used)
                .limit(limit)
                .remaining(limit == -1 ? -1 : Math.max(0, limit - used))
                .allowed(limit == -1 || used < limit)
                .build();
    }

    private int getTodayUsage(Integer accountId, String featureCode) {
        return featureUsageRepository
                .findByAccountIdAndFeatureCodeAndUsageDate(accountId, featureCode, LocalDate.now())
                .map(FeatureUsage::getUsageCount)
                .orElse(0);
    }

    private int getLimit(Integer accountId, String featureCode) {
        boolean hasFeature = subscriptionService.hasFeature(accountId, featureCode)
                || subscriptionService.hasFeature(accountId, featureCode + "_LIMITED")
                || subscriptionService.hasFeature(accountId, featureCode + "_UNLIMITED");

        if (!hasFeature) {
            // Free tier
            return FREE_QUOTAS.getOrDefault(featureCode, 0);
        }

        // Check if unlimited
        if (subscriptionService.hasFeature(accountId, featureCode + "_UNLIMITED")) {
            return -1; // unlimited
        }

        // Paid but limited (e.g., AI_CHAT_LIMITED)
        if (BASIC_QUOTAS.containsKey(featureCode)) {
            return BASIC_QUOTAS.get(featureCode);
        }

        return -1; // Paid, no quota → unlimited
    }

    private String normalizeFeatureCode(String featureCode) {
        // AI_CHAT_LIMITED / AI_CHAT_UNLIMITED → AI_CHAT
        if (featureCode.startsWith("AI_CHAT")) {
            return "AI_CHAT";
        }
        return featureCode;
    }
}
