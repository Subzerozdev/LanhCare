package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ReviewStatus;
import com.lanhcare.entity.TranslationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating ICD11Translation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11TranslationRequest {
    
    @NotBlank(message = "ICD URI is required")
    private String icdUri;
    
    @NotBlank(message = "Vietnamese title is required")
    private String vnTitle;
    
    private String vnDefinition;
    
    @Builder.Default
    private ReviewStatus reviewStatus = ReviewStatus.PENDING;
    
    @Builder.Default
    private TranslationStatus status = TranslationStatus.DRAFT;
}

