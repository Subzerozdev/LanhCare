package com.lanhcare.dto.admin.hospital;

import com.lanhcare.entity.SpecialtyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for MedicalSpecialty response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSpecialtyResponse {
    
    private Integer id;
    private String nameVn;
    private String nameEn;
    private SpecialtyStatus status;
    
    // Hospital info
    private Integer hospitalId;
    private String hospitalName;
    
    // ICD-11 info (if linked)
    private String icdUri;
    private String icdCode;
    private String icdTitle;
}

