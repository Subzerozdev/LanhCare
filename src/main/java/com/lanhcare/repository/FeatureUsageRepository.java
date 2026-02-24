package com.lanhcare.repository;

import com.lanhcare.entity.FeatureUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FeatureUsageRepository extends JpaRepository<FeatureUsage, Integer> {

    Optional<FeatureUsage> findByAccountIdAndFeatureCodeAndUsageDate(
            Integer accountId, String featureCode, LocalDate usageDate);
}
