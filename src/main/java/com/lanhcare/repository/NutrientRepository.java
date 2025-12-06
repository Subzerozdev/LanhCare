package com.lanhcare.repository;

import com.lanhcare.entity.Nutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Nutrient entity
 */
@Repository
public interface NutrientRepository extends JpaRepository<Nutrient, Integer> {
    
    /**
     * Find nutrient by name
     */
    Optional<Nutrient> findByName(String name);
    
    /**
     * Check if nutrient exists by name
     */
    boolean existsByName(String name);
}
