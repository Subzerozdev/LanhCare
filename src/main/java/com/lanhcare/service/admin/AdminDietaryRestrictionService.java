package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionDetailResponse;
import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionRequest;
import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.DietaryRestriction;
import com.lanhcare.entity.ICD11Code;
import com.lanhcare.entity.Nutrient;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.enums.RestrictionStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.DietaryRestrictionRepository;
import com.lanhcare.repository.ICD11CodeRepository;
import com.lanhcare.repository.NutrientRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Dietary Restriction Management Service
 */
@Service
@Transactional
public class AdminDietaryRestrictionService {
    
    private final DietaryRestrictionRepository restrictionRepository;
    private final UserHealthProfileRepository healthProfileRepository;
    private final NutrientRepository nutrientRepository;
    private final ICD11CodeRepository icd11CodeRepository;
    
    public AdminDietaryRestrictionService(
            DietaryRestrictionRepository restrictionRepository,
            UserHealthProfileRepository healthProfileRepository,
            NutrientRepository nutrientRepository,
            ICD11CodeRepository icd11CodeRepository) {
        this.restrictionRepository = restrictionRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.nutrientRepository = nutrientRepository;
        this.icd11CodeRepository = icd11CodeRepository;
    }
    
    /**
     * Get all dietary restrictions with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminDietaryRestrictionResponse> getAllRestrictions(
            String search, 
            RestrictionStatus status,
            Integer userHealthProfileId,
            Integer nutrientId,
            String icdUri,
            int page, 
            int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<DietaryRestriction> restrictionPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            restrictionPage = restrictionRepository.searchRestrictionsByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            restrictionPage = restrictionRepository.searchRestrictions(search, pageable);
        } else if (userHealthProfileId != null) {
            restrictionPage = restrictionRepository.findByUserHealthProfileIdOrderByIdDesc(userHealthProfileId, pageable);
        } else if (nutrientId != null) {
            restrictionPage = restrictionRepository.findByNutrientIdOrderByIdDesc(nutrientId, pageable);
        } else if (icdUri != null && !icdUri.isEmpty()) {
            restrictionPage = restrictionRepository.findByIcdCodeIcdUriOrderByIdDesc(icdUri, pageable);
        } else if (status != null) {
            restrictionPage = restrictionRepository.findByStatusOrderByIdDesc(status, pageable);
        } else {
            restrictionPage = restrictionRepository.findAll(pageable);
        }
        
        List<AdminDietaryRestrictionResponse> restrictions = restrictionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminDietaryRestrictionResponse>builder()
                .content(restrictions)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(restrictionPage.getNumber())
                        .pageSize(restrictionPage.getSize())
                        .totalElements(restrictionPage.getTotalElements())
                        .totalPages(restrictionPage.getTotalPages())
                        .first(restrictionPage.isFirst())
                        .last(restrictionPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get restriction detail by ID
     */
    @Transactional(readOnly = true)
    public AdminDietaryRestrictionDetailResponse getRestrictionDetail(Integer id) {
        DietaryRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary restriction not found with ID: " + id));
        
        return mapToDetailResponse(restriction);
    }
    
    /**
     * Create new dietary restriction
     */
    public AdminDietaryRestrictionResponse createRestriction(AdminDietaryRestrictionRequest request) {
        UserHealthProfile healthProfile = healthProfileRepository.findById(request.getUserHealthProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("User health profile not found with ID: " + request.getUserHealthProfileId()));
        
        DietaryRestriction.DietaryRestrictionBuilder builder = DietaryRestriction.builder()
                .userHealthProfile(healthProfile)
                .name(request.getName())
                .description(request.getDescription())
                .limitType(request.getLimitType())
                .limitValue(request.getLimitValue())
                .limitUnit(request.getLimitUnit())
                .frequency(request.getFrequency())
                .status(request.getStatus() != null ? request.getStatus() : RestrictionStatus.ACTIVE)
                .sourceOfAdvice(request.getSourceOfAdvice());
        
        // Set nutrient if provided
        if (request.getNutrientId() != null) {
            Nutrient nutrient = nutrientRepository.findById(request.getNutrientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nutrient not found with ID: " + request.getNutrientId()));
            builder.nutrient(nutrient);
        }
        
        // Set ICD code if provided
        if (request.getIcdUri() != null && !request.getIcdUri().isEmpty()) {
            ICD11Code icdCode = icd11CodeRepository.findById(request.getIcdUri())
                    .orElseThrow(() -> new ResourceNotFoundException("ICD code not found with URI: " + request.getIcdUri()));
            builder.icdCode(icdCode);
        }
        
        DietaryRestriction saved = restrictionRepository.save(builder.build());
        return mapToResponse(saved);
    }
    
    /**
     * Update dietary restriction
     */
    public AdminDietaryRestrictionResponse updateRestriction(Integer id, AdminDietaryRestrictionRequest request) {
        DietaryRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary restriction not found with ID: " + id));
        
        // Update user health profile if provided
        if (request.getUserHealthProfileId() != null) {
            UserHealthProfile healthProfile = healthProfileRepository.findById(request.getUserHealthProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("User health profile not found with ID: " + request.getUserHealthProfileId()));
            restriction.setUserHealthProfile(healthProfile);
        }
        
        // Update nutrient if provided
        if (request.getNutrientId() != null) {
            Nutrient nutrient = nutrientRepository.findById(request.getNutrientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nutrient not found with ID: " + request.getNutrientId()));
            restriction.setNutrient(nutrient);
        } else if (request.getNutrientId() == null && restriction.getNutrient() != null) {
            // Allow clearing nutrient by sending null
            restriction.setNutrient(null);
        }
        
        // Update ICD code if provided
        if (request.getIcdUri() != null && !request.getIcdUri().isEmpty()) {
            ICD11Code icdCode = icd11CodeRepository.findById(request.getIcdUri())
                    .orElseThrow(() -> new ResourceNotFoundException("ICD code not found with URI: " + request.getIcdUri()));
            restriction.setIcdCode(icdCode);
        } else if (request.getIcdUri() == null && restriction.getIcdCode() != null) {
            // Allow clearing ICD code by sending null
            restriction.setIcdCode(null);
        }
        
        // Update other fields
        if (request.getName() != null) {
            restriction.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restriction.setDescription(request.getDescription());
        }
        if (request.getLimitType() != null) {
            restriction.setLimitType(request.getLimitType());
        }
        if (request.getLimitValue() != null) {
            restriction.setLimitValue(request.getLimitValue());
        }
        if (request.getLimitUnit() != null) {
            restriction.setLimitUnit(request.getLimitUnit());
        }
        if (request.getFrequency() != null) {
            restriction.setFrequency(request.getFrequency());
        }
        if (request.getStatus() != null) {
            restriction.setStatus(request.getStatus());
        }
        if (request.getSourceOfAdvice() != null) {
            restriction.setSourceOfAdvice(request.getSourceOfAdvice());
        }
        
        DietaryRestriction updated = restrictionRepository.save(restriction);
        return mapToResponse(updated);
    }
    
    /**
     * Update restriction status
     */
    public AdminDietaryRestrictionResponse updateRestrictionStatus(Integer id, RestrictionStatus status) {
        DietaryRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary restriction not found with ID: " + id));
        
        restriction.setStatus(status);
        DietaryRestriction updated = restrictionRepository.save(restriction);
        return mapToResponse(updated);
    }
    
    /**
     * Delete restriction (soft delete - set status to INACTIVE)
     */
    public void deleteRestriction(Integer id) {
        DietaryRestriction restriction = restrictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary restriction not found with ID: " + id));
        
        // Soft delete - set status to INACTIVE
        restriction.setStatus(RestrictionStatus.INACTIVE);
        restrictionRepository.save(restriction);
    }
    
    // ========== Helper Methods ==========
    
    private AdminDietaryRestrictionResponse mapToResponse(DietaryRestriction restriction) {
        AdminDietaryRestrictionResponse.AdminDietaryRestrictionResponseBuilder builder = AdminDietaryRestrictionResponse.builder()
                .id(restriction.getId())
                .name(restriction.getName())
                .description(restriction.getDescription())
                .limitType(restriction.getLimitType())
                .limitValue(restriction.getLimitValue())
                .limitUnit(restriction.getLimitUnit())
                .frequency(restriction.getFrequency())
                .status(restriction.getStatus())
                .sourceOfAdvice(restriction.getSourceOfAdvice())
                .userHealthProfileId(restriction.getUserHealthProfile().getId())
                .accountId(restriction.getUserHealthProfile().getAccount().getId())
                .accountEmail(restriction.getUserHealthProfile().getAccount().getEmail());
        
        // Nutrient info
        if (restriction.getNutrient() != null) {
            builder.nutrientId(restriction.getNutrient().getId())
                    .nutrientName(restriction.getNutrient().getName());
        }
        
        // ICD Code info
        if (restriction.getIcdCode() != null) {
            builder.icdUri(restriction.getIcdCode().getIcdUri())
                    .icdCode(restriction.getIcdCode().getIcdCode())
                    .icdTitle(restriction.getIcdCode().getOriginalTitleEn());
        }
        
        return builder.build();
    }
    
    private AdminDietaryRestrictionDetailResponse mapToDetailResponse(DietaryRestriction restriction) {
        AdminDietaryRestrictionDetailResponse.AdminDietaryRestrictionDetailResponseBuilder builder = 
                AdminDietaryRestrictionDetailResponse.builder()
                        .id(restriction.getId())
                        .name(restriction.getName())
                        .description(restriction.getDescription())
                        .limitType(restriction.getLimitType())
                        .limitValue(restriction.getLimitValue())
                        .limitUnit(restriction.getLimitUnit())
                        .frequency(restriction.getFrequency())
                        .status(restriction.getStatus())
                        .sourceOfAdvice(restriction.getSourceOfAdvice())
                        .userHealthProfileId(restriction.getUserHealthProfile().getId())
                        .accountId(restriction.getUserHealthProfile().getAccount().getId())
                        .accountEmail(restriction.getUserHealthProfile().getAccount().getEmail())
                        .accountFullname(restriction.getUserHealthProfile().getAccount().getFullname());
        
        // Nutrient info
        if (restriction.getNutrient() != null) {
            builder.nutrientId(restriction.getNutrient().getId())
                    .nutrientName(restriction.getNutrient().getName())
                    .nutrientUnit(restriction.getNutrient().getUnit());
        }
        
        // ICD Code info
        if (restriction.getIcdCode() != null) {
            ICD11Code icdCode = restriction.getIcdCode();
            builder.icdUri(icdCode.getIcdUri())
                    .icdCode(icdCode.getIcdCode())
                    .icdTitle(icdCode.getOriginalTitleEn())
                    .icdDefinition(icdCode.getDefinitionEn());
        }
        
        return builder.build();
    }
}
