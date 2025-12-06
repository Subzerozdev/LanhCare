package com.lanhcare.repository;

import com.lanhcare.entity.Account;
import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Find accounts by role
     */
    Page<Account> findByRole(AccountRole role, Pageable pageable);
    
    /**
     * Find accounts by status with pagination
     */
    Page<Account> findByStatus(AccountStatus status, Pageable pageable);
    
    /**
     * Find accounts by role and status
     */
    Page<Account> findByRoleAndStatus(AccountRole role, AccountStatus status, Pageable pageable);
    
    /**
     * Search accounts by email or fullname (case insensitive)
     */
    @Query("SELECT a FROM Account a WHERE " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.fullname) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Account> searchAccounts(@Param("search") String search, Pageable pageable);
    
    /**
     * Search accounts with role filter
     */
    @Query("SELECT a FROM Account a WHERE " +
           "(LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.fullname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND a.role = :role")
    Page<Account> searchAccountsByRole(@Param("search") String search, 
                                        @Param("role") AccountRole role, 
                                        Pageable pageable);
    
    /**
     * Search accounts with status filter
     */
    @Query("SELECT a FROM Account a WHERE " +
           "(LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.fullname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND a.status = :status")
    Page<Account> searchAccountsByStatus(@Param("search") String search, 
                                          @Param("status") AccountStatus status, 
                                          Pageable pageable);
    
    /**
     * Search accounts with role and status filters
     */
    @Query("SELECT a FROM Account a WHERE " +
           "(LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.fullname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND a.role = :role AND a.status = :status")
    Page<Account> searchAccountsByRoleAndStatus(@Param("search") String search, 
                                                 @Param("role") AccountRole role,
                                                 @Param("status") AccountStatus status, 
                                                 Pageable pageable);
    
    /**
     * Count accounts by status
     */
    long countByStatus(AccountStatus status);
    
    /**
     * Count accounts by role
     */
    long countByRole(AccountRole role);
}
