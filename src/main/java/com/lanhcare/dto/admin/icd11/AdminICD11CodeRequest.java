package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ICD11Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating ICD11Code
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11CodeRequest {
    
    @NotBlank(message = "ICD URI is required")
    private String icdUri;
    
    @NotBlank(message = "ICD code is required")
    private String icdCode;
    
    private String chapterUri;
    
    private String parentUri;
    
    private String originalTitleEn;
    
    private String definitionEn;
    
    private String exclusionTermsEn;
    
    private Boolean isResidualCategory;
    
    private Boolean isLeaf;
    
    @Builder.Default
    private ICD11Status status = ICD11Status.ACTIVE;
}

