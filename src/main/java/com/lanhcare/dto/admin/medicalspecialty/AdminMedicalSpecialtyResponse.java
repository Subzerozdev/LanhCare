package com.lanhcare.dto.admin.medicalspecialty;

import com.lanhcare.enums.SpecialtyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for medical specialty response (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMedicalSpecialtyResponse {
    
    private Integer id;
    private String nameVn;
    private String nameEn;
    private SpecialtyStatus status;
    
    // Hospital info
    private Integer hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    
    // ICD-11 info (if linked)
    private String icdUri;
    private String icdCode;
    private String icdTitle;
}
