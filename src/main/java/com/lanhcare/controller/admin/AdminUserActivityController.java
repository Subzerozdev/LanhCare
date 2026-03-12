package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.useractivity.AdminUserActivityResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.service.admin.AdminUserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin User Activity Overview Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/user-activity")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - User Activity", description = "Admin APIs for viewing user activity (daily logs, meals, exercises)")
public class AdminUserActivityController {
    
    private final AdminUserActivityService activityService;
    
    public AdminUserActivityController(AdminUserActivityService activityService) {
        this.activityService = activityService;
    }
    
    /**
     * Get user daily activity logs
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user activity logs",
               description = "Get paginated daily logs for a specific user, including meal and exercise summaries")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserActivityResponse>>> getUserActivity(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminUserActivityResponse> activity = activityService.getUserActivity(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success("User activity retrieved successfully", activity));
    }
}
