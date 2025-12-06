package com.lanhcare.repository;

import com.lanhcare.entity.MedicalSpecialty;
import com.lanhcare.entity.SpecialtyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MedicalSpecialty entity
 */
@Repository
public interface MedicalSpecialtyRepository extends JpaRepository<MedicalSpecialty, Integer> {
    
    /**
     * Find specialties by hospital ID
     */
    List<MedicalSpecialty> findByHospitalId(Integer hospitalId);
    
    /**
     * Find specialties by hospital ID and status
     */
    List<MedicalSpecialty> findByHospitalIdAndStatus(Integer hospitalId, SpecialtyStatus status);
    
    /**
     * Count specialties by hospital ID
     */
    long countByHospitalId(Integer hospitalId);
    
    /**
     * Delete all specialties by hospital ID
     */
    void deleteByHospitalId(Integer hospitalId);
}
