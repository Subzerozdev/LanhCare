package com.lanhcare.service.impls;

import com.lanhcare.dto.admin.hospital.AdminHospitalResponse;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.MedicalSpecialty;
import com.lanhcare.enums.HospitalStatus;
import com.lanhcare.repository.HospitalRepository;
import com.lanhcare.repository.MedicalSpecialtyRepository;
import com.lanhcare.service.HospitalService;
import com.lanhcare.service.admin.AdminHospitalService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {
    private final HospitalRepository hospitalRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final AdminHospitalService adminHospitalService;

    @Override
    public PageResponse<AdminHospitalResponse> getAllHospitals(String search, HospitalStatus status, int page, int size) {
        return adminHospitalService.getAllHospitals(search, status, page, size);
    }

    @Transactional(readOnly = true)
    public AdminHospitalResponse getHospitalById(Integer id) {
        return adminHospitalService.getHospitalById(id);
    }

    @Transactional(readOnly = true)
    public List<AdminSpecialtyResponse> getHospitalSpecialties(Integer hospitalId) {
        return adminHospitalService.getHospitalSpecialties(hospitalId);
    }

    @Override
    public List<AdminHospitalResponse> getHospitalBySpecialty(Integer specialtyId) {
        MedicalSpecialty medicalSpecialty = medicalSpecialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new RuntimeException("Medical Specialty Not Found"));

        return medicalSpecialty.getHospital().stream()
                .map(adminHospitalService::mapToResponse).toList();
    }
}
