package com.lanhcare.dto.admin.medicalspecialty;

import com.lanhcare.enums.SpecialtyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating medical specialty (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMedicalSpecialtyRequest {
    
    @NotNull(message = "Hospital ID is required")
    private Integer hospitalId;
    
    @NotBlank(message = "Vietnamese name is required")
    private String nameVn;
    
    @NotBlank(message = "English name is required")
    private String nameEn;
    
    private String icdUri;
    
    @Builder.Default
    private SpecialtyStatus status = SpecialtyStatus.ACTIVE;
}
