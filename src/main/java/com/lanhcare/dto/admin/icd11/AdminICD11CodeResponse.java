package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ICD11Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ICD11Code response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11CodeResponse {
    
    private String icdUri;
    private String icdCode;
    private String originalTitleEn;
    private String definitionEn;
    private String exclusionTermsEn;
    private Boolean isResidualCategory;
    private Boolean isLeaf;
    private LocalDateTime lastSynced;
    private ICD11Status status;
    
    // Chapter info
    private String chapterUri;
    private String chapterCode;
    private String chapterTitle;
    
    // Parent info
    private String parentUri;
    private String parentCode;
    private String parentTitle;
    
    // Translation info (if exists)
    private String vnTitle;
    private String vnDefinition;
    
    // Statistics
    private Long childrenCount;
    private Boolean hasTranslation;
}

