package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.subscription.PurchaseResponse;
import com.lanhcare.dto.subscription.PurchaseSubscriptionRequest;
import com.lanhcare.dto.subscription.SubscriptionResponse;
import com.lanhcare.dto.subscription.TransactionHistoryResponse;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.SubscriptionService;
import com.lanhcare.service.VNPayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Subscription Controller
 * User-facing APIs for subscription management (requires JWT authentication)
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "User - Subscription", description = "APIs for managing user subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final JwtTokenProvider jwtTokenProvider;
    private final VNPayService vnPayService;

    /**
     * Get current user's active subscription
     */
    @GetMapping("/me")
    @Operation(summary = "Get my subscription", 
               description = "Get the current active subscription of the authenticated user. Returns null if on Free plan.")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getMySubscription(
            @RequestHeader("Authorization") String token) {

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        SubscriptionResponse subscription = subscriptionService.getMySubscription(accountId);

        if (subscription == null) {
            return ResponseEntity.ok(ApiResponse.success("Bạn đang sử dụng gói Free", null));
        }

        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved successfully", subscription));
    }

    /**
     * Purchase a subscription (creates VNPay payment URL)
     */
    @PostMapping("/purchase")
    @Operation(summary = "Purchase subscription", 
               description = "Purchase a subscription plan. Returns a VNPay payment URL to complete the payment.")
    public ResponseEntity<ApiResponse<PurchaseResponse>> purchaseSubscription(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody PurchaseSubscriptionRequest request,
            HttpServletRequest httpRequest) {

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        String ipAddress = vnPayService.getIpAddress(httpRequest);

        PurchaseResponse response = subscriptionService.purchaseSubscription(accountId, request, ipAddress);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Payment URL created. Redirect to VNPay to complete payment.", response));
    }

    /**
     * Cancel the current active subscription
     */
    @PutMapping("/cancel")
    @Operation(summary = "Cancel subscription", 
               description = "Cancel the current active subscription. User can still use features until the end date.")
    public ResponseEntity<ApiResponse<Void>> cancelSubscription(
            @RequestHeader("Authorization") String token) {

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        subscriptionService.cancelSubscription(accountId);

        return ResponseEntity.ok(ApiResponse.success("Gói đăng ký đã được hủy thành công", null));
    }

    /**
     * Get user's transaction history
     */
    @GetMapping("/transactions")
    @Operation(summary = "Get my transactions", 
               description = "Get the transaction history of the authenticated user")
    public ResponseEntity<ApiResponse<List<TransactionHistoryResponse>>> getMyTransactions(
            @RequestHeader("Authorization") String token) {

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        List<TransactionHistoryResponse> transactions = subscriptionService.getMyTransactions(accountId);

        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    /**
     * Check if user has a specific feature
     */
    @GetMapping("/features/{featureCode}")
    @Operation(summary = "Check feature access", 
               description = "Check if the authenticated user has access to a specific feature based on their subscription plan")
    public ResponseEntity<ApiResponse<Boolean>> checkFeature(
            @RequestHeader("Authorization") String token,
            @PathVariable String featureCode) {

        int accountId = Integer.parseInt(jwtTokenProvider.getIdentifierFromToken(token));
        boolean hasFeature = subscriptionService.hasFeature(accountId, featureCode);

        return ResponseEntity.ok(ApiResponse.success(
                hasFeature ? "Feature available" : "Feature requires subscription upgrade",
                hasFeature));
    }
}
