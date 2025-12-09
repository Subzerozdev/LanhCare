package com.lanhcare.dto.admin.icd11;

import com.lanhcare.entity.ICD11Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating ICD11Chapter
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminICD11ChapterRequest {
    
    @NotBlank(message = "Chapter URI is required")
    private String chapterUri;
    
    @NotBlank(message = "Chapter code is required")
    private String chapterCode;
    
    private String vnTitle;
    
    private String originalTitleEn;
    
    private String releaseId;
    
    @Builder.Default
    private ICD11Status status = ICD11Status.ACTIVE;
}

