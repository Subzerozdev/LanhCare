package com.lanhcare.service;

import com.lanhcare.dto.translator.TranslatorResponse;

public interface TranslatorService {
    TranslatorResponse translateToVietnamese(String text);
}
