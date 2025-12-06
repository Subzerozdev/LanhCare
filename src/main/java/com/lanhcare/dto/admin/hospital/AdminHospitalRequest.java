package com.lanhcare.dto.admin.hospital;

import com.lanhcare.entity.HospitalStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating Hospital
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminHospitalRequest {
    
    @NotBlank(message = "Hospital name is required")
    private String name;
    
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    @Builder.Default
    private HospitalStatus status = HospitalStatus.ACTIVE;
}
