package com.lanhcare.repository;

import com.lanhcare.entity.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FCMTokenRepository extends JpaRepository<FCMToken, Integer> {
    List<FCMToken> findByAccountId(Integer accountId);
}

