package com.lanhcare.repository;

import com.lanhcare.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    
    /**
     * Find all transactions for an account
     */
    List<Transaction> findByAccountIdOrderByIdDesc(Integer accountId);
}
