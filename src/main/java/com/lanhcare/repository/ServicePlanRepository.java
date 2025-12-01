package com.lanhcare.repository;

import com.lanhcare.entity.ServicePlan;
import com.lanhcare.entity.ServicePlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
