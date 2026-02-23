package com.lanhcare.controller;

import com.lanhcare.dto.admin.serviceplan.AdminServicePlanResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.dto.subscription.ServicePlanResponse;
import com.lanhcare.entity.ServicePlan;
import com.lanhcare.enums.ServicePlanStatus;
import com.lanhcare.repository.ServicePlanRepository;
import com.lanhcare.service.admin.AdminServicePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/service-plans")
@RequiredArgsConstructor
@Tag(name = "User - Service Plan View", description = "Customer APIs for viewing service plans")
public class ServicePlanController {
    private final AdminServicePlanService servicePlanService;
    private final ServicePlanRepository servicePlanRepository;

    @GetMapping
    @Operation(summary = "Get all service plans (paginated)", description = "Get paginated list of service plans with optional filters")
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
     * Simple list of ACTIVE plans for mobile app (no pagination, features as array)
     */
    @GetMapping("/list")
    @Operation(summary = "Get active service plans for mobile",
               description = "Returns a simple list of all ACTIVE service plans with features as array. Designed for mobile app.")
    public ResponseEntity<ApiResponse<List<ServicePlanResponse>>> getActivePlansForMobile() {
        List<ServicePlan> plans = servicePlanRepository.findByStatusOrderByPriceAsc(ServicePlanStatus.ACTIVE);

        List<ServicePlanResponse> response = plans.stream()
                .map(this::mapToServicePlanResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Active service plans retrieved", response));
    }

    private ServicePlanResponse mapToServicePlanResponse(ServicePlan plan) {
        List<String> featureList = Collections.emptyList();
        if (plan.getFeatures() != null && !plan.getFeatures().isEmpty()) {
            featureList = Arrays.asList(plan.getFeatures().split(","));
        }

        return ServicePlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .periodValue(plan.getPeriodValue())
                .periodUnit(plan.getPeriodUnit())
                .features(featureList)
                .build();
    }
}

