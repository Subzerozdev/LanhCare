package com.lanhcare.repository;

import com.lanhcare.entity.Account;
import com.lanhcare.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    
    /**
     * Find account by email
     */
    Optional<Account> findByEmail(String email);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all accounts by status
     */
    List<Account> findByStatus(AccountStatus status);
    
    /**
     * Find all active accounts
     */
    default List<Account> findAllActive() {
        return findByStatus(AccountStatus.ACTIVE);
    }
}
