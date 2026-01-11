package com.lanhcare.controller;

import com.lanhcare.dto.admin.serviceplan.AdminServicePlanResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.ServicePlanStatus;
import com.lanhcare.service.admin.AdminServicePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/service-plans")
@RequiredArgsConstructor
@Tag(name = "User - Service Plan View", description = "Customer APIs for viewing service plans")
public class ServicePlanController {
    private final AdminServicePlanService servicePlanService;

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
}
