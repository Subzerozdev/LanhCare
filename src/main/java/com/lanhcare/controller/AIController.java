package com.lanhcare.controller;

import com.lanhcare.dto.ai.AIRequest;
import com.lanhcare.dto.ai.AIResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.subscription.FeatureQuotaResponse;
import com.lanhcare.exception.exps.FeatureNotAvailableException;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AIService;
import com.lanhcare.service.FeatureGateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/ai")
@Tag(name = "User - AI Chatbot", description = "APIs for user AI Chatbot")
public class AIController {
    private final AIService aiService;
    private final FeatureGateService featureGateService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/prompt")
    @Operation(summary = "Send AI prompt", description = "Send a message to AI chatbot. Requires login. Free: 3/day, Basic: 10/day, Premium: unlimited.")
    public ResponseEntity<?> promptAI(
            @RequestBody AIRequest request,
            @RequestHeader(value = "Authorization", required = false) String token
    ) throws IOException {

        // 1. Require authentication
        if (token == null || token.isEmpty()) {
            request.setIsMember(false);
            request.setToken(null);
            // Allow unauthenticated users to still use AI (but no quota tracking)
            log.info("AI Request (unauthenticated): {}", request);
            AIResponse response = aiService.generateResponse(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // 2. Check AI Chat quota
        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));

        boolean canAccess = featureGateService.canAccess(accountId, "AI_CHAT");
        if (!canAccess) {
            FeatureQuotaResponse quota = featureGateService.getQuota(accountId, "AI_CHAT");
            throw new FeatureNotAvailableException("AI_CHAT",
                    String.format("Bạn đã hết lượt chat AI hôm nay (%d/%d). Nâng cấp gói để chat thêm.",
                            quota.getUsed(), quota.getLimit()));
        }

        // 3. Process AI request
        request.setIsMember(true);
        request.setToken(token);
        log.info("AI Request (account={}): {}", accountId, request);
        AIResponse response = aiService.generateResponse(request);

        // 4. Record usage AFTER successful response
        featureGateService.recordUsage(accountId, "AI_CHAT");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get AI Chat quota for the current user
     */
    @GetMapping("/quota")
    @Operation(summary = "Get AI chat quota", description = "Returns how many AI chat messages the user has used and remaining today")
    public ResponseEntity<ApiResponse<FeatureQuotaResponse>> getAiQuota(
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        if (token == null || token.isEmpty()) {
            // Unauthenticated — return Free defaults
            FeatureQuotaResponse quota = FeatureQuotaResponse.builder()
                    .featureCode("AI_CHAT")
                    .used(0)
                    .limit(3)
                    .remaining(3)
                    .allowed(true)
                    .build();
            return ResponseEntity.ok(ApiResponse.success("AI quota (unauthenticated)", quota));
        }

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        FeatureQuotaResponse quota = featureGateService.getQuota(accountId, "AI_CHAT");

        return ResponseEntity.ok(ApiResponse.success("AI quota retrieved", quota));
    }
}
