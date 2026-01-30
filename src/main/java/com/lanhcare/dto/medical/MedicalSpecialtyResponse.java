package com.lanhcare.dto.medical;

import com.lanhcare.enums.SpecialtyStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalSpecialtyResponse {
    private Integer id;
    private String nameVn;
    private String nameEn;
    private SpecialtyStatus status;
}