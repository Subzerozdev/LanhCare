package com.lanhcare.service;

import com.lanhcare.dto.icd.IcdEntityDTO;
import com.lanhcare.dto.icd.IcdEntityRequest;
import com.lanhcare.dto.icd.IcdTokenResponse;
import com.lanhcare.dto.icd.ReleaseDTO;
import com.lanhcare.entity.ICD11Chapter;

import java.util.List;

public interface IcdApiService {
    void seedChaptersData();

    void seedSampleCodesData();

    void seedICDCode(IcdEntityRequest request);

    ICD11Chapter getChapterById(String id);

    List<ICD11Chapter> seedICDData();

    ReleaseDTO fetchReleaseChapters();

    List<String> getChapterCodes(ReleaseDTO releaseDTO);

    IcdTokenResponse getAccessToken();

    IcdEntityDTO fetchIcdEntity(String entityId);
}
