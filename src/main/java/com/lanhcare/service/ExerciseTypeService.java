package com.lanhcare.service;

import com.lanhcare.dto.exercisetype.ExerciseTypeRequest;
import com.lanhcare.dto.exercisetype.ExerciseTypeResponse;
import com.lanhcare.entity.ExerciseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ExerciseTypeService {
    ExerciseType create(ExerciseTypeRequest request);
    ExerciseType update(Integer id, ExerciseTypeRequest request);
    ExerciseType getById(Integer id);
    Page<ExerciseTypeResponse> getAll(Pageable pageable);
    Page<ExerciseTypeResponse> searchByActivity(String keyword, Pageable pageable);
    void delete(Integer id);
    ExerciseTypeResponse mapToResponse(ExerciseType exerciseType);
}
