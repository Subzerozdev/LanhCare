package com.lanhcare.dto.admin.hospital;

import com.lanhcare.entity.SpecialtyStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating medical specialty
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSpecialtyRequest {
    
    @NotBlank(message = "Vietnamese name is required")
    private String nameVn;
    
    @NotBlank(message = "English name is required")
    private String nameEn;
    
    private String icdUri;
    
    @Builder.Default
    private SpecialtyStatus status = SpecialtyStatus.ACTIVE;
}
