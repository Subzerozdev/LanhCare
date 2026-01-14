package com.lanhcare.repository;

import com.lanhcare.entity.ExerciseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {
    Page<ExerciseType> findAllByDeletedFalse(Pageable pageable);
    Page<ExerciseType> findByActivityContainingIgnoreCaseOrExamplesContainingIgnoreCaseAndDeletedIsFalse(String activity, String examples, Pageable pageable);
}