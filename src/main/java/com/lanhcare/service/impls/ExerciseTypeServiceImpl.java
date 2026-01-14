package com.lanhcare.service.impls;

import com.lanhcare.dto.exercisetype.ExerciseTypeRequest;
import com.lanhcare.dto.exercisetype.ExerciseTypeResponse;
import com.lanhcare.entity.ExerciseType;
import com.lanhcare.exception.exps.ExerciseException;
import com.lanhcare.repository.ExerciseTypeRepository;
import com.lanhcare.service.ExerciseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseTypeServiceImpl implements ExerciseTypeService {
    private final ExerciseTypeRepository exerciseTypeRepository;

    @Override
    public ExerciseType create(ExerciseTypeRequest request) {
        ExerciseType type = ExerciseType.builder()
                .activity(request.getActivity())
                .examples(request.getExamples())
                .metValue(request.getMetValue())
                .build();
        return exerciseTypeRepository.save(type);
    }

    @Override
    public ExerciseType update(Integer id, ExerciseTypeRequest request) {
        ExerciseType type = getById(id);

        Optional.ofNullable(request.getActivity()).ifPresent(type::setActivity);
        Optional.ofNullable(request.getExamples()).ifPresent(type::setExamples);
        Optional.ofNullable(request.getMetValue()).ifPresent(type::setMetValue);

        return exerciseTypeRepository.save(type);
    }

    @Override
    public ExerciseType getById(Integer id) {
        ExerciseType exerciseType = exerciseTypeRepository.findById(id)
                .orElseThrow(() -> new ExerciseException("Exercise Type not found"));

        if (exerciseType.isDeleted()){
            throw new ExerciseException("Exercise Type deleted");
        }

        return exerciseType;
    }

    @Override
    public Page<ExerciseTypeResponse> getAll(Pageable pageable) {
        return exerciseTypeRepository.findAllByDeletedFalse(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExerciseTypeResponse> searchByActivity(
            String keyword, Pageable pageable
    ) {
        return exerciseTypeRepository.findByActivityContainingIgnoreCaseOrExamplesContainingIgnoreCaseAndDeletedIsFalse(
                keyword, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void delete(Integer id) {
        ExerciseType exerciseType = getById(id);
        exerciseType.setDeleted(true);
        exerciseTypeRepository.save(exerciseType);
    }

    @Override
    public ExerciseTypeResponse mapToResponse(ExerciseType entity) {
        return ExerciseTypeResponse.builder()
                .id(entity.getId())
                .activity(entity.getActivity())
                .examples(entity.getExamples())
                .metValue(entity.getMetValue())
                .build();
    }
}