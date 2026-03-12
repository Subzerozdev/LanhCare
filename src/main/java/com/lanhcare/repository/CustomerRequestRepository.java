package com.lanhcare.repository;

import com.lanhcare.entity.CustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Integer> {
    Optional<CustomerRequest> findByVerificationCode(String verificationCode);
    
    // ========== ADMIN QUERIES ==========
    
    Page<CustomerRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<CustomerRequest> findByStatusOrderByCreatedAtDesc(CustomerRequest.RequestStatus status, Pageable pageable);
    
    long countByStatus(CustomerRequest.RequestStatus status);
}
