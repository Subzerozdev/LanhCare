package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ICD11Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ICD11Chapter response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11ChapterResponse {
    
    private String chapterUri;
    private String chapterCode;
    private String vnTitle;
    private String originalTitleEn;
    private String releaseId;
    private ICD11Status status;
    
    // Statistics
    private Long codeCount;
}

