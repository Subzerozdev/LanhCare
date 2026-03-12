package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.dashboard.AdminDashboardResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.service.admin.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Dashboard Controller
 * Provides overview statistics for admin panel
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Dashboard", description = "Admin APIs for dashboard overview and statistics")
public class AdminDashboardController {
    
    private final AdminDashboardService dashboardService;
    
    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    /**
     * Get dashboard overview with all statistics
     */
    @GetMapping
    @Operation(summary = "Get dashboard overview", 
               description = "Get comprehensive dashboard with user stats, revenue, subscriptions, and moderation queue")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboardOverview() {
        AdminDashboardResponse dashboard = dashboardService.getDashboardOverview();
        return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved successfully", dashboard));
    }
}
