package com.lanhcare.repository;

import com.lanhcare.entity.CustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Integer> {
    Optional<CustomerRequest> findByVerificationCode(String verificationCode);
}
