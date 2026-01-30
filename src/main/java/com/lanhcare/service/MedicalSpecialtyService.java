package com.lanhcare.service;

import com.lanhcare.dto.medical.MedicalSpecialtyResponse;
import com.lanhcare.entity.MedicalSpecialty;

import java.util.List;

public interface MedicalSpecialtyService {
    MedicalSpecialty getById(int id);
    List<MedicalSpecialty> getByICD11(String icdUri);
    List<MedicalSpecialty> getByHospital(int hospitalId);
    MedicalSpecialtyResponse mapToResponse(MedicalSpecialty entity);
}
