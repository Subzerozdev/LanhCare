package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.serviceplan.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.ServicePlan;
import com.lanhcare.entity.Transaction;
import com.lanhcare.enums.ServicePlanStatus;
import com.lanhcare.enums.TransactionStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.ServicePlanRepository;
import com.lanhcare.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Service Plan Management Service
 */
@Service
@Transactional
public class AdminServicePlanService {
    
    private final ServicePlanRepository servicePlanRepository;
    private final TransactionRepository transactionRepository;
    
    public AdminServicePlanService(ServicePlanRepository servicePlanRepository,
                                    TransactionRepository transactionRepository) {
        this.servicePlanRepository = servicePlanRepository;
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Get all service plans with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminServicePlanResponse> getAllServicePlans(String search, ServicePlanStatus status,
                                                                       int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ServicePlan> planPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            planPage = servicePlanRepository.searchServicePlansByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            planPage = servicePlanRepository.searchServicePlans(search, pageable);
        } else if (status != null) {
            planPage = servicePlanRepository.findByStatusOrderByIdDesc(status, pageable);
        } else {
            planPage = servicePlanRepository.findAllByOrderByIdDesc(pageable);
        }
        
        List<AdminServicePlanResponse> plans = planPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminServicePlanResponse>builder()
                .content(plans)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(planPage.getNumber())
                        .pageSize(planPage.getSize())
                        .totalElements(planPage.getTotalElements())
                        .totalPages(planPage.getTotalPages())
                        .first(planPage.isFirst())
                        .last(planPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get service plan detail by ID
     */
    @Transactional(readOnly = true)
    public AdminServicePlanDetailResponse getServicePlanDetail(Integer id) {
        ServicePlan plan = servicePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service plan not found with ID: " + id));
        
        return mapToDetailResponse(plan);
    }
    
    /**
     * Create new service plan
     */
    public AdminServicePlanResponse createServicePlan(AdminCreateServicePlanRequest request) {
        ServicePlan plan = ServicePlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .periodValue(request.getPeriodValue())
                .periodUnit(request.getPeriodUnit())
                .status(request.getStatus() != null ? request.getStatus() : ServicePlanStatus.ACTIVE)
                .build();
        
        ServicePlan saved = servicePlanRepository.save(plan);
        return mapToResponse(saved);
    }
    
    /**
     * Update service plan
     */
    public AdminServicePlanResponse updateServicePlan(Integer id, AdminUpdateServicePlanRequest request) {
        ServicePlan plan = servicePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service plan not found with ID: " + id));
        
        // Update fields if provided
        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            plan.setPrice(request.getPrice());
        }
        if (request.getPeriodValue() != null) {
            plan.setPeriodValue(request.getPeriodValue());
        }
        if (request.getPeriodUnit() != null) {
            plan.setPeriodUnit(request.getPeriodUnit());
        }
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }
        
        ServicePlan updated = servicePlanRepository.save(plan);
        return mapToResponse(updated);
    }
    
    /**
     * Change service plan status
     */
    public AdminServicePlanResponse changeServicePlanStatus(Integer id, ServicePlanStatus status) {
        ServicePlan plan = servicePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service plan not found with ID: " + id));
        
        plan.setStatus(status);
        ServicePlan updated = servicePlanRepository.save(plan);
        return mapToResponse(updated);
    }
    
    /**
     * Delete service plan (soft delete - set status to INACTIVE)
     */
    public void deleteServicePlan(Integer id) {
        ServicePlan plan = servicePlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service plan not found with ID: " + id));
        
        // Soft delete - set status to INACTIVE
        plan.setStatus(ServicePlanStatus.INACTIVE);
        servicePlanRepository.save(plan);
    }
    
    // ========== Private Helper Methods ==========
    
    private AdminServicePlanResponse mapToResponse(ServicePlan plan) {
        // Calculate transaction stats
        long transactionCount = transactionRepository.countByServicePlanId(plan.getId());
        
        return AdminServicePlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .periodValue(plan.getPeriodValue())
                .periodUnit(plan.getPeriodUnit())
                .status(plan.getStatus())
                .transactionCount(transactionCount)
                .build();
    }
    
    private AdminServicePlanDetailResponse mapToDetailResponse(ServicePlan plan) {
        // Get transaction stats
        List<Transaction> transactions = transactionRepository.findByServicePlanIdOrderByTransactionDateDesc(plan.getId());
        
        long transactionCount = transactions.size();
        long activeSubscriptions = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .count();
        
        BigDecimal totalRevenue = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return AdminServicePlanDetailResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .periodValue(plan.getPeriodValue())
                .periodUnit(plan.getPeriodUnit())
                .status(plan.getStatus())
                .transactionCount(transactionCount)
                .activeSubscriptions(activeSubscriptions)
                .totalRevenue(totalRevenue)
                .build();
    }
}
