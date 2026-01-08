package com.lanhcare.repository;

import com.lanhcare.entity.ServicePlan;
import com.lanhcare.enums.ServicePlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ServicePlan entity
 */
@Repository
public interface ServicePlanRepository extends JpaRepository<ServicePlan, Integer> {

    /**
     * Find all active service plans
     */
    List<ServicePlan> findByStatus(ServicePlanStatus status);

    /**
     * Find all active plans
     */
    default List<ServicePlan> findAllActive() {
        return findByStatus(ServicePlanStatus.ACTIVE);
    }

    // ========== Admin Management Methods ==========

    /**
     * Find by status with pagination
     */
    Page<ServicePlan> findByStatusOrderByIdDesc(ServicePlanStatus status, Pageable pageable);

    /**
     * Find all with pagination
     */
    Page<ServicePlan> findAllByOrderByIdDesc(Pageable pageable);

    /**
     * Search service plans by name or description
     */
    @Query("SELECT sp FROM ServicePlan sp WHERE " +
            "LOWER(sp.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(sp.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ServicePlan> searchServicePlans(@Param("search") String search, Pageable pageable);

    /**
     * Search service plans by name or description with status filter
     */
    @Query("SELECT sp FROM ServicePlan sp WHERE " +
            "(LOWER(sp.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(sp.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "sp.status = :status")
    Page<ServicePlan> searchServicePlansByStatus(@Param("search") String search,
                                                 @Param("status") ServicePlanStatus status,
                                                 Pageable pageable);
}