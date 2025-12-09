package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.icd11.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.*;
import com.lanhcare.exception.ResourceAlreadyExistsException;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.ICD11ChapterRepository;
import com.lanhcare.repository.ICD11CodeRepository;
import com.lanhcare.repository.ICD11TranslationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin ICD-11 Management Service
 * Handles Chapters, Codes, and Translations
 */
@Service
@Transactional
public class AdminICD11Service {
    
    private final ICD11ChapterRepository chapterRepository;
    private final ICD11CodeRepository codeRepository;
    private final ICD11TranslationRepository translationRepository;
    
    public AdminICD11Service(ICD11ChapterRepository chapterRepository,
                             ICD11CodeRepository codeRepository,
                             ICD11TranslationRepository translationRepository) {
        this.chapterRepository = chapterRepository;
        this.codeRepository = codeRepository;
        this.translationRepository = translationRepository;
    }
    
    // ==================== CHAPTERS ====================
    
    /**
     * Get all chapters with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminICD11ChapterResponse> getAllChapters(String search, ICD11Status status,
                                                                   int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ICD11Chapter> chapterPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            chapterPage = chapterRepository.searchChaptersByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            chapterPage = chapterRepository.searchChapters(search, pageable);
        } else if (status != null) {
            chapterPage = chapterRepository.findByStatusOrderByChapterCodeAsc(status, pageable);
        } else {
            chapterPage = chapterRepository.findAllByOrderByChapterCodeAsc(pageable);
        }
        
        List<AdminICD11ChapterResponse> chapters = chapterPage.getContent().stream()
                .map(this::mapChapterToResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(chapters, chapterPage);
    }
    
    /**
     * Get all active chapters (for dropdowns)
     */
    @Transactional(readOnly = true)
    public List<AdminICD11ChapterResponse> getAllActiveChapters() {
        return chapterRepository.findAllActive().stream()
                .map(this::mapChapterToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get chapter by URI
     */
    @Transactional(readOnly = true)
    public AdminICD11ChapterResponse getChapterByUri(String chapterUri) {
        ICD11Chapter chapter = chapterRepository.findById(chapterUri)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + chapterUri));
        return mapChapterToResponse(chapter);
    }
    
    /**
     * Create chapter
     */
    public AdminICD11ChapterResponse createChapter(AdminICD11ChapterRequest request) {
        if (chapterRepository.existsById(request.getChapterUri())) {
            throw new ResourceAlreadyExistsException("ICD11Chapter", "chapterUri", request.getChapterUri());
        }
        
        ICD11Chapter chapter = ICD11Chapter.builder()
                .chapterUri(request.getChapterUri())
                .chapterCode(request.getChapterCode())
                .vnTitle(request.getVnTitle())
                .originalTitleEn(request.getOriginalTitleEn())
                .releaseId(request.getReleaseId())
                .status(request.getStatus() != null ? request.getStatus() : ICD11Status.ACTIVE)
                .build();
        
        ICD11Chapter saved = chapterRepository.save(chapter);
        return mapChapterToResponse(saved);
    }
    
    /**
     * Update chapter
     */
    public AdminICD11ChapterResponse updateChapter(String chapterUri, AdminICD11ChapterRequest request) {
        ICD11Chapter chapter = chapterRepository.findById(chapterUri)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + chapterUri));
        
        chapter.setChapterCode(request.getChapterCode());
        chapter.setVnTitle(request.getVnTitle());
        chapter.setOriginalTitleEn(request.getOriginalTitleEn());
        chapter.setReleaseId(request.getReleaseId());
        
        if (request.getStatus() != null) {
            chapter.setStatus(request.getStatus());
        }
        
        ICD11Chapter updated = chapterRepository.save(chapter);
        return mapChapterToResponse(updated);
    }
    
    /**
     * Update chapter status
     */
    public AdminICD11ChapterResponse updateChapterStatus(String chapterUri, ICD11Status status) {
        ICD11Chapter chapter = chapterRepository.findById(chapterUri)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + chapterUri));
        
        chapter.setStatus(status);
        ICD11Chapter updated = chapterRepository.save(chapter);
        return mapChapterToResponse(updated);
    }
    
    /**
     * Delete chapter (soft delete)
     */
    public void deleteChapter(String chapterUri) {
        ICD11Chapter chapter = chapterRepository.findById(chapterUri)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + chapterUri));
        
        chapter.setStatus(ICD11Status.DEPRECATED);
        chapterRepository.save(chapter);
    }
    
    // ==================== CODES ====================
    
    /**
     * Get all codes with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminICD11CodeResponse> getAllCodes(String search, String chapterUri, 
                                                             ICD11Status status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ICD11Code> codePage;
        
        if (chapterUri != null && !chapterUri.isEmpty() && search != null && !search.isEmpty()) {
            codePage = codeRepository.searchCodesInChapter(chapterUri, search, pageable);
        } else if (search != null && !search.isEmpty() && status != null) {
            codePage = codeRepository.searchCodesByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            codePage = codeRepository.searchCodes(search, pageable);
        } else if (chapterUri != null && !chapterUri.isEmpty() && status != null) {
            codePage = codeRepository.findByChapterChapterUriAndStatusOrderByIcdCodeAsc(chapterUri, status, pageable);
        } else if (chapterUri != null && !chapterUri.isEmpty()) {
            codePage = codeRepository.findByChapterChapterUriOrderByIcdCodeAsc(chapterUri, pageable);
        } else if (status != null) {
            codePage = codeRepository.findByStatusOrderByIcdCodeAsc(status, pageable);
        } else {
            codePage = codeRepository.findAllByOrderByIcdCodeAsc(pageable);
        }
        
        List<AdminICD11CodeResponse> codes = codePage.getContent().stream()
                .map(this::mapCodeToResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(codes, codePage);
    }
    
    /**
     * Get code by URI
     */
    @Transactional(readOnly = true)
    public AdminICD11CodeResponse getCodeByUri(String icdUri) {
        ICD11Code code = codeRepository.findById(icdUri)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with URI: " + icdUri));
        return mapCodeToResponse(code);
    }
    
    /**
     * Get children of a code
     */
    @Transactional(readOnly = true)
    public List<AdminICD11CodeResponse> getCodeChildren(String parentUri) {
        return codeRepository.findByParentIcdUriOrderByIcdCodeAsc(parentUri).stream()
                .map(this::mapCodeToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Create code
     */
    public AdminICD11CodeResponse createCode(AdminICD11CodeRequest request) {
        if (codeRepository.existsById(request.getIcdUri())) {
            throw new ResourceAlreadyExistsException("ICD11Code", "icdUri", request.getIcdUri());
        }
        
        ICD11Code.ICD11CodeBuilder builder = ICD11Code.builder()
                .icdUri(request.getIcdUri())
                .icdCode(request.getIcdCode())
                .originalTitleEn(request.getOriginalTitleEn())
                .definitionEn(request.getDefinitionEn())
                .exclusionTermsEn(request.getExclusionTermsEn())
                .isResidualCategory(request.getIsResidualCategory())
                .isLeaf(request.getIsLeaf())
                .lastSynced(LocalDateTime.now())
                .status(request.getStatus() != null ? request.getStatus() : ICD11Status.ACTIVE);
        
        // Set chapter if provided
        if (request.getChapterUri() != null && !request.getChapterUri().isEmpty()) {
            ICD11Chapter chapter = chapterRepository.findById(request.getChapterUri())
                    .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + request.getChapterUri()));
            builder.chapter(chapter);
        }
        
        // Set parent if provided
        if (request.getParentUri() != null && !request.getParentUri().isEmpty()) {
            ICD11Code parent = codeRepository.findById(request.getParentUri())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent code not found with URI: " + request.getParentUri()));
            builder.parent(parent);
        }
        
        ICD11Code saved = codeRepository.save(builder.build());
        return mapCodeToResponse(saved);
    }
    
    /**
     * Update code
     */
    public AdminICD11CodeResponse updateCode(String icdUri, AdminICD11CodeRequest request) {
        ICD11Code code = codeRepository.findById(icdUri)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with URI: " + icdUri));
        
        code.setIcdCode(request.getIcdCode());
        code.setOriginalTitleEn(request.getOriginalTitleEn());
        code.setDefinitionEn(request.getDefinitionEn());
        code.setExclusionTermsEn(request.getExclusionTermsEn());
        code.setIsResidualCategory(request.getIsResidualCategory());
        code.setIsLeaf(request.getIsLeaf());
        code.setLastSynced(LocalDateTime.now());
        
        if (request.getStatus() != null) {
            code.setStatus(request.getStatus());
        }
        
        // Update chapter if provided
        if (request.getChapterUri() != null && !request.getChapterUri().isEmpty()) {
            ICD11Chapter chapter = chapterRepository.findById(request.getChapterUri())
                    .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with URI: " + request.getChapterUri()));
            code.setChapter(chapter);
        }
        
        // Update parent if provided
        if (request.getParentUri() != null && !request.getParentUri().isEmpty()) {
            ICD11Code parent = codeRepository.findById(request.getParentUri())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent code not found with URI: " + request.getParentUri()));
            code.setParent(parent);
        } else {
            code.setParent(null);
        }
        
        ICD11Code updated = codeRepository.save(code);
        return mapCodeToResponse(updated);
    }
    
    /**
     * Update code status
     */
    public AdminICD11CodeResponse updateCodeStatus(String icdUri, ICD11Status status) {
        ICD11Code code = codeRepository.findById(icdUri)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with URI: " + icdUri));
        
        code.setStatus(status);
        ICD11Code updated = codeRepository.save(code);
        return mapCodeToResponse(updated);
    }
    
    /**
     * Delete code (soft delete)
     */
    public void deleteCode(String icdUri) {
        ICD11Code code = codeRepository.findById(icdUri)
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with URI: " + icdUri));
        
        code.setStatus(ICD11Status.DEPRECATED);
        codeRepository.save(code);
    }
    
    // ==================== TRANSLATIONS ====================
    
    /**
     * Get all translations with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminICD11TranslationResponse> getAllTranslations(String search, 
                                                                           TranslationStatus status,
                                                                           ReviewStatus reviewStatus,
                                                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ICD11Translation> translationPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            translationPage = translationRepository.searchTranslationsByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            translationPage = translationRepository.searchTranslations(search, pageable);
        } else if (reviewStatus != null) {
            translationPage = translationRepository.findByReviewStatusOrderByIdDesc(reviewStatus, pageable);
        } else if (status != null) {
            translationPage = translationRepository.findByStatusOrderByIdDesc(status, pageable);
        } else {
            translationPage = translationRepository.findAllByOrderByIdDesc(pageable);
        }
        
        List<AdminICD11TranslationResponse> translations = translationPage.getContent().stream()
                .map(this::mapTranslationToResponse)
                .collect(Collectors.toList());
        
        return buildPageResponse(translations, translationPage);
    }
    
    /**
     * Get translation by ID
     */
    @Transactional(readOnly = true)
    public AdminICD11TranslationResponse getTranslationById(Integer id) {
        ICD11Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));
        return mapTranslationToResponse(translation);
    }
    
    /**
     * Get translations by ICD code
     */
    @Transactional(readOnly = true)
    public List<AdminICD11TranslationResponse> getTranslationsByCode(String icdUri) {
        return translationRepository.findByIcdCodeIcdUri(icdUri).stream()
                .map(this::mapTranslationToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Create translation
     */
    public AdminICD11TranslationResponse createTranslation(AdminICD11TranslationRequest request) {
        ICD11Code code = codeRepository.findById(request.getIcdUri())
                .orElseThrow(() -> new ResourceNotFoundException("Code not found with URI: " + request.getIcdUri()));
        
        ICD11Translation translation = ICD11Translation.builder()
                .icdCode(code)
                .vnTitle(request.getVnTitle())
                .vnDefinition(request.getVnDefinition())
                .reviewStatus(request.getReviewStatus() != null ? request.getReviewStatus() : ReviewStatus.PENDING)
                .status(request.getStatus() != null ? request.getStatus() : TranslationStatus.DRAFT)
                .build();
        
        ICD11Translation saved = translationRepository.save(translation);
        return mapTranslationToResponse(saved);
    }
    
    /**
     * Update translation
     */
    public AdminICD11TranslationResponse updateTranslation(Integer id, AdminICD11TranslationRequest request) {
        ICD11Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));
        
        translation.setVnTitle(request.getVnTitle());
        translation.setVnDefinition(request.getVnDefinition());
        
        if (request.getReviewStatus() != null) {
            translation.setReviewStatus(request.getReviewStatus());
        }
        
        if (request.getStatus() != null) {
            translation.setStatus(request.getStatus());
        }
        
        ICD11Translation updated = translationRepository.save(translation);
        return mapTranslationToResponse(updated);
    }
    
    /**
     * Update translation review status
     */
    public AdminICD11TranslationResponse updateTranslationReviewStatus(Integer id, ReviewStatus reviewStatus) {
        ICD11Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));
        
        translation.setReviewStatus(reviewStatus);
        
        // Auto publish if approved
        if (reviewStatus == ReviewStatus.APPROVED) {
            translation.setStatus(TranslationStatus.PUBLISHED);
        }
        
        ICD11Translation updated = translationRepository.save(translation);
        return mapTranslationToResponse(updated);
    }
    
    /**
     * Delete translation (soft delete)
     */
    public void deleteTranslation(Integer id) {
        ICD11Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with ID: " + id));
        
        translation.setStatus(TranslationStatus.ARCHIVED);
        translationRepository.save(translation);
    }
    
    // ==================== HELPER METHODS ====================
    
    private AdminICD11ChapterResponse mapChapterToResponse(ICD11Chapter chapter) {
        long codeCount = codeRepository.countByChapterChapterUri(chapter.getChapterUri());
        
        return AdminICD11ChapterResponse.builder()
                .chapterUri(chapter.getChapterUri())
                .chapterCode(chapter.getChapterCode())
                .vnTitle(chapter.getVnTitle())
                .originalTitleEn(chapter.getOriginalTitleEn())
                .releaseId(chapter.getReleaseId())
                .status(chapter.getStatus())
                .codeCount(codeCount)
                .build();
    }
    
    private AdminICD11CodeResponse mapCodeToResponse(ICD11Code code) {
        AdminICD11CodeResponse.AdminICD11CodeResponseBuilder builder = AdminICD11CodeResponse.builder()
                .icdUri(code.getIcdUri())
                .icdCode(code.getIcdCode())
                .originalTitleEn(code.getOriginalTitleEn())
                .definitionEn(code.getDefinitionEn())
                .exclusionTermsEn(code.getExclusionTermsEn())
                .isResidualCategory(code.getIsResidualCategory())
                .isLeaf(code.getIsLeaf())
                .lastSynced(code.getLastSynced())
                .status(code.getStatus());
        
        // Chapter info
        if (code.getChapter() != null) {
            builder.chapterUri(code.getChapter().getChapterUri())
                    .chapterCode(code.getChapter().getChapterCode())
                    .chapterTitle(code.getChapter().getVnTitle() != null ? 
                            code.getChapter().getVnTitle() : code.getChapter().getOriginalTitleEn());
        }
        
        // Parent info
        if (code.getParent() != null) {
            builder.parentUri(code.getParent().getIcdUri())
                    .parentCode(code.getParent().getIcdCode())
                    .parentTitle(code.getParent().getOriginalTitleEn());
        }
        
        // Translation info
        translationRepository.findByIcdCodeIcdUriAndStatus(code.getIcdUri(), TranslationStatus.PUBLISHED)
                .ifPresent(translation -> {
                    builder.vnTitle(translation.getVnTitle())
                            .vnDefinition(translation.getVnDefinition())
                            .hasTranslation(true);
                });
        
        if (builder.build().getHasTranslation() == null) {
            builder.hasTranslation(translationRepository.existsByIcdCodeIcdUri(code.getIcdUri()));
        }
        
        // Children count
        builder.childrenCount((long) codeRepository.findByParentIcdUriOrderByIcdCodeAsc(code.getIcdUri()).size());
        
        return builder.build();
    }
    
    private AdminICD11TranslationResponse mapTranslationToResponse(ICD11Translation translation) {
        AdminICD11TranslationResponse.AdminICD11TranslationResponseBuilder builder = 
                AdminICD11TranslationResponse.builder()
                        .id(translation.getId())
                        .vnTitle(translation.getVnTitle())
                        .vnDefinition(translation.getVnDefinition())
                        .reviewStatus(translation.getReviewStatus())
                        .status(translation.getStatus());
        
        // ICD Code info
        if (translation.getIcdCode() != null) {
            ICD11Code code = translation.getIcdCode();
            builder.icdUri(code.getIcdUri())
                    .icdCode(code.getIcdCode())
                    .originalTitleEn(code.getOriginalTitleEn())
                    .definitionEn(code.getDefinitionEn());
            
            // Chapter info
            if (code.getChapter() != null) {
                builder.chapterCode(code.getChapter().getChapterCode())
                        .chapterTitle(code.getChapter().getVnTitle() != null ?
                                code.getChapter().getVnTitle() : code.getChapter().getOriginalTitleEn());
            }
        }
        
        return builder.build();
    }
    
    private <T> PageResponse<T> buildPageResponse(List<T> content, Page<?> page) {
        return PageResponse.<T>builder()
                .content(content)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(page.getNumber())
                        .pageSize(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .build())
                .build();
    }
}

