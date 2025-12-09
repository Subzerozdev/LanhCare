package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ReviewStatus;
import com.lanhcare.entity.TranslationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ICD11Translation response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11TranslationResponse {
    
    private Integer id;
    private String vnTitle;
    private String vnDefinition;
    private ReviewStatus reviewStatus;
    private TranslationStatus status;
    
    // ICD Code info
    private String icdUri;
    private String icdCode;
    private String originalTitleEn;
    private String definitionEn;
    
    // Chapter info
    private String chapterCode;
    private String chapterTitle;
}

