package com.lanhcare.dto.admin.hospital;

import com.lanhcare.entity.HospitalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Hospital response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminHospitalResponse {
    
    private Integer id;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private HospitalStatus status;
    private Integer specialtyCount;
}
