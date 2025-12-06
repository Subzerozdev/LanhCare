package com.lanhcare.repository;

import com.lanhcare.entity.Hospital;
import com.lanhcare.entity.HospitalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Hospital entity
 */
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    
    /**
     * Find hospitals by status
     */
    List<Hospital> findByStatus(HospitalStatus status);
    
    /**
     * Find all active hospitals
     */
    default List<Hospital> findAllActive() {
        return findByStatus(HospitalStatus.ACTIVE);
    }
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Find all hospitals with pagination
     */
    Page<Hospital> findAllByOrderByIdDesc(Pageable pageable);
    
    /**
     * Find hospitals by status with pagination
     */
    Page<Hospital> findByStatusOrderByIdDesc(HospitalStatus status, Pageable pageable);
    
    /**
     * Search hospitals by name or address
     */
    @Query("SELECT h FROM Hospital h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(h.address) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY h.id DESC")
    Page<Hospital> searchHospitals(@Param("search") String search, Pageable pageable);
    
    /**
     * Search hospitals by name or address with status filter
     */
    @Query("SELECT h FROM Hospital h WHERE (LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(h.address) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND h.status = :status ORDER BY h.id DESC")
    Page<Hospital> searchHospitalsByStatus(@Param("search") String search,
                                            @Param("status") HospitalStatus status,
                                            Pageable pageable);
    
    /**
     * Count hospitals by status
     */
    long countByStatus(HospitalStatus status);
}
