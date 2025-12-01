package com.lanhcare.repository;

import com.lanhcare.entity.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for FoodType entity
 */
@Repository
public interface FoodTypeRepository extends JpaRepository<FoodType, Integer> {
}
