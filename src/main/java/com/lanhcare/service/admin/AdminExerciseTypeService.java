package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.exercisetype.AdminExerciseTypeRequest;
import com.lanhcare.dto.admin.exercisetype.AdminExerciseTypeResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.ExerciseType;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.ExerciseTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Exercise Type Management Service
 */
@Service
@Transactional
public class AdminExerciseTypeService {
    
    private final ExerciseTypeRepository exerciseTypeRepository;
    
    public AdminExerciseTypeService(ExerciseTypeRepository exerciseTypeRepository) {
        this.exerciseTypeRepository = exerciseTypeRepository;
    }
    
    /**
     * Get all exercise types with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminExerciseTypeResponse> getAllExerciseTypes(
            String search,
            Boolean includeDeleted,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ExerciseType> exerciseTypePage;
        
        if (search != null && !search.isEmpty()) {
            if (Boolean.TRUE.equals(includeDeleted)) {
                exerciseTypePage = exerciseTypeRepository.findByActivityContainingIgnoreCaseOrExamplesContainingIgnoreCaseAndDeletedIsFalse(
                        search, search, pageable);
            } else {
                exerciseTypePage = exerciseTypeRepository.findByActivityContainingIgnoreCaseOrExamplesContainingIgnoreCaseAndDeletedIsFalse(
                        search, search, pageable);
            }
        } else if (Boolean.TRUE.equals(includeDeleted)) {
            exerciseTypePage = exerciseTypeRepository.findAll(pageable);
        } else {
            exerciseTypePage = exerciseTypeRepository.findAllByDeletedFalse(pageable);
        }
        
        List<AdminExerciseTypeResponse> exerciseTypes = exerciseTypePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminExerciseTypeResponse>builder()
                .content(exerciseTypes)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(exerciseTypePage.getNumber())
                        .pageSize(exerciseTypePage.getSize())
                        .totalElements(exerciseTypePage.getTotalElements())
                        .totalPages(exerciseTypePage.getTotalPages())
                        .first(exerciseTypePage.isFirst())
                        .last(exerciseTypePage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get exercise type detail by ID
     */
    @Transactional(readOnly = true)
    public AdminExerciseTypeResponse getExerciseTypeDetail(Integer id) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with ID: " + id));
        
        return mapToResponse(exerciseType);
    }
    
    /**
     * Create new exercise type
     */
    public AdminExerciseTypeResponse createExerciseType(AdminExerciseTypeRequest request) {
        ExerciseType exerciseType = ExerciseType.builder()
                .activity(request.getActivity())
                .examples(request.getExamples())
                .metValue(request.getMetValue())
                .deleted(false)
                .build();
        
        ExerciseType saved = exerciseTypeRepository.save(exerciseType);
        return mapToResponse(saved);
    }
    
    /**
     * Update exercise type
     */
    public AdminExerciseTypeResponse updateExerciseType(Integer id, AdminExerciseTypeRequest request) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with ID: " + id));
        
        if (request.getActivity() != null) {
            exerciseType.setActivity(request.getActivity());
        }
        if (request.getExamples() != null) {
            exerciseType.setExamples(request.getExamples());
        }
        if (request.getMetValue() != null) {
            exerciseType.setMetValue(request.getMetValue());
        }
        
        ExerciseType updated = exerciseTypeRepository.save(exerciseType);
        return mapToResponse(updated);
    }
    
    /**
     * Delete exercise type (soft delete)
     */
    public void deleteExerciseType(Integer id) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with ID: " + id));
        
        // Soft delete - set deleted to true
        exerciseType.setDeleted(true);
        exerciseTypeRepository.save(exerciseType);
    }
    
    /**
     * Restore deleted exercise type
     */
    public AdminExerciseTypeResponse restoreExerciseType(Integer id) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with ID: " + id));
        
        exerciseType.setDeleted(false);
        ExerciseType restored = exerciseTypeRepository.save(exerciseType);
        return mapToResponse(restored);
    }
    
    // ========== Helper Methods ==========
    
    private AdminExerciseTypeResponse mapToResponse(ExerciseType exerciseType) {
        return AdminExerciseTypeResponse.builder()
                .id(exerciseType.getId())
                .activity(exerciseType.getActivity())
                .examples(exerciseType.getExamples())
                .metValue(exerciseType.getMetValue())
                .deleted(exerciseType.isDeleted())
                .build();
    }
}
