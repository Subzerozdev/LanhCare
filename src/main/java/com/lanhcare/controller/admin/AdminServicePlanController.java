package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.serviceplan.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.ServicePlanStatus;
import com.lanhcare.service.admin.AdminServicePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Service Plan Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/service-plans")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Service Plan Management", description = "Admin APIs for managing service plans")
public class AdminServicePlanController {
    
    private final AdminServicePlanService servicePlanService;
    
    public AdminServicePlanController(AdminServicePlanService servicePlanService) {
        this.servicePlanService = servicePlanService;
    }
    
    /**
     * Get all service plans with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all service plans", description = "Get paginated list of service plans with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminServicePlanResponse>>> getAllServicePlans(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ServicePlanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminServicePlanResponse> plans = servicePlanService.getAllServicePlans(
                search, status, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Service plans retrieved successfully", plans));
    }
    
    /**
     * Get service plan detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get service plan detail", description = "Get detailed information about a service plan")
    public ResponseEntity<ApiResponse<AdminServicePlanDetailResponse>> getServicePlanDetail(@PathVariable Integer id) {
        AdminServicePlanDetailResponse plan = servicePlanService.getServicePlanDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Service plan retrieved successfully", plan));
    }
    
    /**
     * Create new service plan
     */
    @PostMapping
    @Operation(summary = "Create service plan", description = "Create a new service plan")
    public ResponseEntity<ApiResponse<AdminServicePlanResponse>> createServicePlan(
            @Valid @RequestBody AdminCreateServicePlanRequest request) {
        
        AdminServicePlanResponse plan = servicePlanService.createServicePlan(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Service plan created successfully", plan));
    }
    
    /**
     * Update service plan
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update service plan", description = "Update service plan information")
    public ResponseEntity<ApiResponse<AdminServicePlanResponse>> updateServicePlan(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUpdateServicePlanRequest request) {
        
        AdminServicePlanResponse plan = servicePlanService.updateServicePlan(id, request);
        return ResponseEntity.ok(ApiResponse.success("Service plan updated successfully", plan));
    }
    
    /**
     * Change service plan status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change service plan status", description = "Change service plan status")
    public ResponseEntity<ApiResponse<AdminServicePlanResponse>> changeServicePlanStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AdminChangeServicePlanStatusRequest request) {
        
        AdminServicePlanResponse plan = servicePlanService.changeServicePlanStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Service plan status updated successfully", plan));
    }
    
    /**
     * Delete service plan (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete service plan", description = "Soft delete a service plan (sets status to INACTIVE)")
    public ResponseEntity<ApiResponse<Void>> deleteServicePlan(@PathVariable Integer id) {
        servicePlanService.deleteServicePlan(id);
        return ResponseEntity.ok(ApiResponse.success("Service plan deleted successfully", null));
    }
}
