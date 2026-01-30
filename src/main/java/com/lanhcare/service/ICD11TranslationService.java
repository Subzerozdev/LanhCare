package com.lanhcare.service;

import com.lanhcare.dto.icd.ICD11TranslationResponse;
import com.lanhcare.entity.ICD11Translation;

import java.util.List;

public interface ICD11TranslationService {
    List<ICD11Translation> searchByKeyword(String keyword);
    ICD11Translation getById(int id);
    ICD11TranslationResponse mapToResponse(ICD11Translation entity);
}
