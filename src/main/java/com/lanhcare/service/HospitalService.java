package com.lanhcare.service;

import com.lanhcare.dto.admin.hospital.AdminHospitalResponse;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.HospitalStatus;

import java.util.List;

public interface HospitalService {
    PageResponse<AdminHospitalResponse> getAllHospitals(
            String search, HospitalStatus status, int page, int size
    );

    AdminHospitalResponse getHospitalById(Integer id);

    List<AdminSpecialtyResponse> getHospitalSpecialties(Integer hospitalId);

    List<AdminHospitalResponse> getHospitalBySpecialty(Integer specialtyId);
}
