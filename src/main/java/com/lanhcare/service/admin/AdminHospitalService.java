package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.hospital.AdminHospitalRequest;
import com.lanhcare.dto.admin.hospital.AdminHospitalResponse;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyRequest;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.*;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.HospitalRepository;
import com.lanhcare.repository.MedicalSpecialtyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Hospital Management Service
 */
@Service
@Transactional
public class AdminHospitalService {
    
    private final HospitalRepository hospitalRepository;
    private final MedicalSpecialtyRepository specialtyRepository;
    
    public AdminHospitalService(HospitalRepository hospitalRepository,
                                 MedicalSpecialtyRepository specialtyRepository) {
        this.hospitalRepository = hospitalRepository;
        this.specialtyRepository = specialtyRepository;
    }
    
    /**
     * Get all hospitals with pagination
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminHospitalResponse> getAllHospitals(String search, HospitalStatus status,
                                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Hospital> hospitalPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            hospitalPage = hospitalRepository.searchHospitalsByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            hospitalPage = hospitalRepository.searchHospitals(search, pageable);
        } else if (status != null) {
            hospitalPage = hospitalRepository.findByStatusOrderByIdDesc(status, pageable);
        } else {
            hospitalPage = hospitalRepository.findAllByOrderByIdDesc(pageable);
        }
        
        List<AdminHospitalResponse> hospitals = hospitalPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminHospitalResponse>builder()
                .content(hospitals)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(hospitalPage.getNumber())
                        .pageSize(hospitalPage.getSize())
                        .totalElements(hospitalPage.getTotalElements())
                        .totalPages(hospitalPage.getTotalPages())
                        .first(hospitalPage.isFirst())
                        .last(hospitalPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get hospital by ID
     */
    @Transactional(readOnly = true)
    public AdminHospitalResponse getHospitalById(Integer id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));
        return mapToResponse(hospital);
    }
    
    /**
     * Create hospital
     */
    public AdminHospitalResponse createHospital(AdminHospitalRequest request) {
        Hospital hospital = Hospital.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(request.getStatus())
                .build();
        
        Hospital saved = hospitalRepository.save(hospital);
        return mapToResponse(saved);
    }
    
    /**
     * Update hospital
     */
    public AdminHospitalResponse updateHospital(Integer id, AdminHospitalRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));
        
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setLatitude(request.getLatitude());
        hospital.setLongitude(request.getLongitude());
        hospital.setStatus(request.getStatus());
        
        Hospital updated = hospitalRepository.save(hospital);
        return mapToResponse(updated);
    }
    
    /**
     * Update hospital status
     */
    public AdminHospitalResponse updateHospitalStatus(Integer id, HospitalStatus status) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));
        
        hospital.setStatus(status);
        Hospital updated = hospitalRepository.save(hospital);
        return mapToResponse(updated);
    }
    
    /**
     * Delete hospital (soft delete - set status to INACTIVE)
     */
    public void deleteHospital(Integer id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + id));
        
        // Soft delete
        hospital.setStatus(HospitalStatus.INACTIVE);
        hospitalRepository.save(hospital);
    }
    
    // ========== SPECIALTY MANAGEMENT ==========
    
    /**
     * Get all specialties for a hospital
     */
    @Transactional(readOnly = true)
    public List<AdminSpecialtyResponse> getHospitalSpecialties(Integer hospitalId) {
        // Verify hospital exists
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with ID: " + hospitalId);
        }
        
        return specialtyRepository.findByHospitalIdOrderByNameVnAsc(hospitalId).stream()
                .map(this::mapToSpecialtyResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get specialty by ID
     */
    @Transactional(readOnly = true)
    public AdminSpecialtyResponse getSpecialtyById(Integer hospitalId, Integer specialtyId) {
        MedicalSpecialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with ID: " + specialtyId));
        
        if (!specialty.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Specialty does not belong to this hospital");
        }
        
        return mapToSpecialtyResponse(specialty);
    }
    
    /**
     * Add specialty to hospital
     */
    public AdminSpecialtyResponse addSpecialty(Integer hospitalId, AdminSpecialtyRequest request) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with ID: " + hospitalId));
        
        MedicalSpecialty specialty = MedicalSpecialty.builder()
                .hospital(hospital)
                .nameVn(request.getNameVn())
                .nameEn(request.getNameEn())
                .status(request.getStatus() != null ? request.getStatus() : SpecialtyStatus.ACTIVE)
                .build();
        
        MedicalSpecialty saved = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(saved);
    }
    
    /**
     * Update specialty
     */
    public AdminSpecialtyResponse updateSpecialty(Integer hospitalId, Integer specialtyId, 
                                                   AdminSpecialtyRequest request) {
        MedicalSpecialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with ID: " + specialtyId));
        
        if (!specialty.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Specialty does not belong to this hospital");
        }
        
        specialty.setNameVn(request.getNameVn());
        specialty.setNameEn(request.getNameEn());
        
        if (request.getStatus() != null) {
            specialty.setStatus(request.getStatus());
        }
        
        MedicalSpecialty updated = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(updated);
    }
    
    /**
     * Update specialty status
     */
    public AdminSpecialtyResponse updateSpecialtyStatus(Integer hospitalId, Integer specialtyId, 
                                                         SpecialtyStatus status) {
        MedicalSpecialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with ID: " + specialtyId));
        
        if (!specialty.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Specialty does not belong to this hospital");
        }
        
        specialty.setStatus(status);
        MedicalSpecialty updated = specialtyRepository.save(specialty);
        return mapToSpecialtyResponse(updated);
    }
    
    /**
     * Delete specialty
     */
    public void deleteSpecialty(Integer hospitalId, Integer specialtyId) {
        MedicalSpecialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty not found with ID: " + specialtyId));
        
        if (!specialty.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Specialty does not belong to this hospital");
        }
        
        specialtyRepository.delete(specialty);
    }
    
    // ========== Helper Methods ==========
    
    private AdminHospitalResponse mapToResponse(Hospital hospital) {
        long specialtyCount = specialtyRepository.countByHospitalId(hospital.getId());
        
        return AdminHospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .latitude(hospital.getLatitude())
                .longitude(hospital.getLongitude())
                .status(hospital.getStatus())
                .specialtyCount((int) specialtyCount)
                .build();
    }
    
    private AdminSpecialtyResponse mapToSpecialtyResponse(MedicalSpecialty specialty) {
        AdminSpecialtyResponse.AdminSpecialtyResponseBuilder builder = AdminSpecialtyResponse.builder()
                .id(specialty.getId())
                .nameVn(specialty.getNameVn())
                .nameEn(specialty.getNameEn())
                .status(specialty.getStatus())
                .hospitalId(specialty.getHospital().getId())
                .hospitalName(specialty.getHospital().getName());
        
        // Add ICD-11 info if linked
        if (specialty.getIcdCode() != null) {
            builder.icdUri(specialty.getIcdCode().getIcdUri())
                    .icdCode(specialty.getIcdCode().getIcdCode())
                    .icdTitle(specialty.getIcdCode().getOriginalTitleEn());
        }
        
        return builder.build();
    }
}
