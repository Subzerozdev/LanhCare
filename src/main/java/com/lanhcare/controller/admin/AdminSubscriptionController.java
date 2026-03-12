package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.subscription.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.SubscriptionStatus;
import com.lanhcare.service.admin.AdminSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Subscription Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/subscriptions")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Subscription Management", description = "Admin APIs for managing user subscriptions")
public class AdminSubscriptionController {
    
    private final AdminSubscriptionService subscriptionService;
    
    public AdminSubscriptionController(AdminSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    /**
     * Get all subscriptions with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all subscriptions", 
               description = "Get paginated list of subscriptions with optional filters by status and user")
    public ResponseEntity<ApiResponse<PageResponse<AdminSubscriptionResponse>>> getAllSubscriptions(
            @RequestParam(required = false) SubscriptionStatus status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminSubscriptionResponse> subscriptions = subscriptionService.getAllSubscriptions(
                status, userId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved successfully", subscriptions));
    }
    
    /**
     * Get subscription detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get subscription detail", 
               description = "Get detailed information about a subscription including transaction details")
    public ResponseEntity<ApiResponse<AdminSubscriptionDetailResponse>> getSubscriptionDetail(
            @PathVariable Integer id) {
        
        AdminSubscriptionDetailResponse subscription = subscriptionService.getSubscriptionDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved successfully", subscription));
    }
    
    /**
     * Update subscription status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update subscription status", 
               description = "Change subscription status (ACTIVE, EXPIRED, CANCELLED, PENDING)")
    public ResponseEntity<ApiResponse<AdminSubscriptionResponse>> updateSubscriptionStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUpdateSubscriptionRequest request) {
        
        AdminSubscriptionResponse subscription = subscriptionService.updateSubscriptionStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Subscription status updated successfully", subscription));
    }
    
    /**
     * Extend subscription
     */
    @PatchMapping("/{id}/extend")
    @Operation(summary = "Extend subscription", 
               description = "Extend subscription end date by specified number of days. Reactivates expired subscriptions.")
    public ResponseEntity<ApiResponse<AdminSubscriptionResponse>> extendSubscription(
            @PathVariable Integer id,
            @RequestParam int days) {
        
        AdminSubscriptionResponse subscription = subscriptionService.extendSubscription(id, days);
        return ResponseEntity.ok(ApiResponse.success("Subscription extended successfully", subscription));
    }
    
    /**
     * Get expiring subscriptions
     */
    @GetMapping("/expiring")
    @Operation(summary = "Get expiring subscriptions", 
               description = "Get subscriptions that are expiring within the specified number of days")
    public ResponseEntity<ApiResponse<PageResponse<AdminSubscriptionResponse>>> getExpiringSubscriptions(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminSubscriptionResponse> subscriptions = subscriptionService.getExpiringSubscriptions(
                days, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Expiring subscriptions retrieved successfully", subscriptions));
    }
    
    /**
     * Get subscription statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get subscription statistics", 
               description = "Get comprehensive subscription statistics including counts by status and plan")
    public ResponseEntity<ApiResponse<AdminSubscriptionStatsResponse>> getSubscriptionStats() {
        AdminSubscriptionStatsResponse stats = subscriptionService.getSubscriptionStats();
        return ResponseEntity.ok(ApiResponse.success("Subscription statistics retrieved successfully", stats));
    }
}
