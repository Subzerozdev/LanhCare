package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.medicalspecialty.AdminMedicalSpecialtyRequest;
import com.lanhcare.dto.admin.medicalspecialty.AdminMedicalSpecialtyResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.Hospital;
import com.lanhcare.entity.ICD11Code;
import com.lanhcare.entity.MedicalSpecialty;
import com.lanhcare.enums.SpecialtyStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.HospitalRepository;
import com.lanhcare.repository.ICD11CodeRepository;
import com.lanhcare.repository.MedicalSpecialtyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Medical Specialty Management Service
 */
@Service
@Transactional
public class AdminMedicalSpecialtyService {
    
    private final MedicalSpecialtyRepository specialtyRepository;
    private final HospitalRepository hospitalRepository;
    private final ICD11CodeRepository icd11CodeRepository;
    
    public AdminMedicalSpecialtyService(
            MedicalSpecialtyRepository specialtyRepository,
            HospitalRepository hospitalRepository,
            ICD11CodeRepository icd11CodeRepository) {
        this.specialtyRepository = specialtyRepository;
        this.hospitalRepository = hospitalRepository;
        this.icd11CodeRepository = icd11CodeRepository;
    }
    
    /**
     * Get all medical specialties with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminMedicalSpecialtyResponse> getAllSpecialties(
            String search,
            SpecialtyStatus status,
            Integer hospitalId,
            String icdUri,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MedicalSpecialty> specialtyPage;
        
        if (search != null && !search.isEmpty() && status != null && hospitalId != null) {
            specialtyPage = specialtyRepository.searchByHospitalIdAndStatusAndName(
                    hospitalId, status, search, pageable);
        } else if (search != null && !search.isEmpty() && hospitalId != null) {
            specialtyPage = specialtyRepository.searchByHospitalIdAndName(
                    hospitalId, search, pageable);
        } else if (hospitalId != null && status != null) {
            specialtyPage = specialtyRepository.findByHospitalIdAndStatus(hospitalId, status, pageable);
        } else if (hospitalId != null) {
            specialtyPage = specialtyRepository.findByHospitalId(hospitalId, pageable);
        } else if (status != null) {
            specialtyPage = specialtyRepository.findByStatus(status, pageable);
        } else if (icdUri != null && !icdUri.isEmpty()) {
            specialtyPage = specialtyRepository.findByIcdCodeIcdUri(icdUri, pageable);
        } else {
            specialtyPage = specialtyRepository.findAll(pageable);
        }
        
        List<AdminMedicalSpecialtyResponse> specialties = specialtyPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminMedicalSpecialtyResponse>builder()
                .content(specialties)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(specialtyPage.getNumber())
                        .pageSize(specialtyPage.getSize())
                        .totalElements(specialtyPage.getTotalElements())
                        .totalPages(specialtyPage.getTotalPages())
                        .first(specialtyPage.isFirst())
                        .last(specialtyPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get specialty detail by ID
     */
    @Transactional(readOnly = true)
    public AdminMedicalSpecialtyResponse getSpecialtyDetail(Integer id) {
        MedicalSpecialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical specialty not found with ID: " + id));
        
        return mapToResponse(specialty);
    }
    
    /**
     * Create new medical specialty
     */
    public AdminMedicalSpecialtyResponse createSpecialty(AdminMedicalSpecialtyRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + request.getHospitalId()));
        
        MedicalSpecialty.MedicalSpecialtyBuilder builder = MedicalSpecialty.builder()
//                .hospital(hospital)
                .nameVn(request.getNameVn())
                .nameEn(request.getNameEn())
                .status(request.getStatus() != null ? request.getStatus() : SpecialtyStatus.ACTIVE);
        
        // Set ICD code if provided
        if (request.getIcdUri() != null && !request.getIcdUri().isEmpty()) {
            ICD11Code icdCode = icd11CodeRepository.findById(request.getIcdUri())
                    .orElseThrow(() -> new ResourceNotFoundException("ICD code not found with URI: " + request.getIcdUri()));
//            builder.icdCode(icdCode);
        }
        
        MedicalSpecialty saved = specialtyRepository.save(builder.build());
        return mapToResponse(saved);
    }
    
    /**
     * Update medical specialty
     */
    public AdminMedicalSpecialtyResponse updateSpecialty(Integer id, AdminMedicalSpecialtyRequest request) {
        MedicalSpecialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical specialty not found with ID: " + id));
        
        // Update hospital if provided
        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + request.getHospitalId()));
//            specialty.setHospital(hospital);
        }
        
        // Update name fields
        if (request.getNameVn() != null) {
            specialty.setNameVn(request.getNameVn());
        }
        if (request.getNameEn() != null) {
            specialty.setNameEn(request.getNameEn());
        }
        
        // Update ICD code if provided
        if (request.getIcdUri() != null && !request.getIcdUri().isEmpty()) {
            ICD11Code icdCode = icd11CodeRepository.findById(request.getIcdUri())
                    .orElseThrow(() -> new ResourceNotFoundException("ICD code not found with URI: " + request.getIcdUri()));
//            specialty.setIcdCode(icdCode);
        } else if (request.getIcdUri() == null && specialty.getIcdCode() != null) {
            // Allow clearing ICD code by sending null
            specialty.setIcdCode(null);
        }
        
        // Update status
        if (request.getStatus() != null) {
            specialty.setStatus(request.getStatus());
        }
        
        MedicalSpecialty updated = specialtyRepository.save(specialty);
        return mapToResponse(updated);
    }
    
    /**
     * Update specialty status
     */
    public AdminMedicalSpecialtyResponse updateSpecialtyStatus(Integer id, SpecialtyStatus status) {
        MedicalSpecialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical specialty not found with ID: " + id));
        
        specialty.setStatus(status);
        MedicalSpecialty updated = specialtyRepository.save(specialty);
        return mapToResponse(updated);
    }
    
    /**
     * Delete specialty (soft delete - set status to INACTIVE)
     */
    public void deleteSpecialty(Integer id) {
        MedicalSpecialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical specialty not found with ID: " + id));
        
        // Soft delete - set status to INACTIVE
        specialty.setStatus(SpecialtyStatus.INACTIVE);
        specialtyRepository.save(specialty);
    }
    
    // ========== Helper Methods ==========
    
    private AdminMedicalSpecialtyResponse mapToResponse(MedicalSpecialty specialty) {
        AdminMedicalSpecialtyResponse.AdminMedicalSpecialtyResponseBuilder builder = AdminMedicalSpecialtyResponse.builder()
                .id(specialty.getId())
                .nameVn(specialty.getNameVn())
                .nameEn(specialty.getNameEn())
                .status(specialty.getStatus());
//                .hospitalId(specialty.getHospital().getId())
//                .hospitalName(specialty.getHospital().getName())
//                .hospitalAddress(specialty.getHospital().getAddress());
        
        // ICD Code info
//        if (specialty.getIcdCode() != null) {
//            builder.icdUri(specialty.getIcdCode().getIcdUri())
//                    .icdCode(specialty.getIcdCode().getIcdCode())
//                    .icdTitle(specialty.getIcdCode().getOriginalTitleEn());
//        }
        
        return builder.build();
    }
}
