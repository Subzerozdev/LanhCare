package com.lanhcare.service.impls;

import com.lanhcare.dto.icd.ICD11TranslationResponse;
import com.lanhcare.entity.ICD11Translation;
import com.lanhcare.enums.TranslationStatus;
import com.lanhcare.repository.ICD11TranslationRepository;
import com.lanhcare.service.ICD11TranslationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ICD11TranslationServiceImpl implements ICD11TranslationService {
    private final ICD11TranslationRepository translationRepository;

    @Override
    public List<ICD11Translation> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return translationRepository.findAll();
        }
        return translationRepository.searchTranslationsByStatus(
                keyword, TranslationStatus.PUBLISHED
        );
    }

    @Override
    public ICD11Translation getById(int id) {
        return translationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Translation not found with id: " + id));
    }

    @Override
    public ICD11TranslationResponse mapToResponse(ICD11Translation entity) {
        if (entity == null) return null;

        return ICD11TranslationResponse.builder()
                .id(entity.getId())
                .icdUri(entity.getIcdCode() != null ? entity.getIcdCode().getIcdUri() : null)
                .vnTitle(entity.getVnTitle())
                .vnDefinition(entity.getVnDefinition())
                .status(entity.getStatus())
                .build();
    }
}
