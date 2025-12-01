package com.lanhcare.repository;

import com.lanhcare.entity.UserHealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserHealthProfile entity
 */
@Repository
public interface UserHealthProfileRepository extends JpaRepository<UserHealthProfile, Integer> {
    
    /**
     * Find health profile by account ID
     */
    Optional<UserHealthProfile> findByAccountId(Integer accountId);
    
    /**
     * Check if health profile exists for account
     */
    boolean existsByAccountId(Integer accountId);
}
