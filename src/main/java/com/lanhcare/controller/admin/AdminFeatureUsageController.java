package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.featureusage.AdminFeatureUsageStatsResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.service.admin.AdminFeatureUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Admin Feature Usage Analytics Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/feature-usage")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Feature Usage Analytics", description = "Admin APIs for analyzing feature usage patterns")
public class AdminFeatureUsageController {
    
    private final AdminFeatureUsageService featureUsageService;
    
    public AdminFeatureUsageController(AdminFeatureUsageService featureUsageService) {
        this.featureUsageService = featureUsageService;
    }
    
    /**
     * Get overall feature usage statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get feature usage stats", 
               description = "Get total usage counts and unique users per feature. Optionally filter by date range.")
    public ResponseEntity<ApiResponse<AdminFeatureUsageStatsResponse>> getOverallStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        AdminFeatureUsageStatsResponse stats = featureUsageService.getOverallStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Feature usage stats retrieved", stats));
    }
    
    /**
     * Get daily trend for a specific feature
     */
    @GetMapping("/trend/{featureCode}")
    @Operation(summary = "Get feature usage trend", 
               description = "Get daily usage trend for a specific feature code (e.g., MEAL_LOG, AI_CHAT)")
    public ResponseEntity<ApiResponse<List<AdminFeatureUsageStatsResponse.DailyTrend>>> getFeatureTrend(
            @PathVariable String featureCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<AdminFeatureUsageStatsResponse.DailyTrend> trend = featureUsageService.getFeatureTrend(
                featureCode, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Feature trend retrieved", trend));
    }
}
