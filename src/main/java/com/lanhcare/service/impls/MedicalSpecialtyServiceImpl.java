package com.lanhcare.service.impls;

import com.lanhcare.dto.medical.MedicalSpecialtyResponse;
import com.lanhcare.entity.Hospital;
import com.lanhcare.entity.ICD11Code;
import com.lanhcare.entity.MedicalSpecialty;
import com.lanhcare.repository.HospitalRepository;
import com.lanhcare.repository.ICD11CodeRepository;
import com.lanhcare.repository.MedicalSpecialtyRepository;
import com.lanhcare.service.MedicalSpecialtyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalSpecialtyServiceImpl implements MedicalSpecialtyService {

    private final MedicalSpecialtyRepository specialtyRepository;
    private final ICD11CodeRepository icdCodeRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    public MedicalSpecialty getById(int id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialty not found with id: " + id));
    }

    @Override
    public List<MedicalSpecialty> getByICD11(String icdUri) {
        ICD11Code icd = icdCodeRepository.findById(icdUri)
                .orElseThrow(() -> new EntityNotFoundException("ICD11 Code not found: " + icdUri));

        return icd.getMedicalSpecialties();
    }

    @Override
    public List<MedicalSpecialty> getByHospital(int hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException("Hospital not found with id: " + hospitalId));

        return hospital.getMedicalSpecialties();
    }

    @Override
    public MedicalSpecialtyResponse mapToResponse(MedicalSpecialty entity) {
        if (entity == null) return null;

        return MedicalSpecialtyResponse.builder()
                .id(entity.getId())
                .nameVn(entity.getNameVn())
                .nameEn(entity.getNameEn())
                .status(entity.getStatus())
                .build();
    }
}