package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.deletionrequest.AdminDeletionRequestResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.CustomerRequest;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.CustomerRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Deletion Request Management Service
 */
@Service
@Transactional
public class AdminDeletionRequestService {
    
    private final CustomerRequestRepository requestRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminDeletionRequestService(CustomerRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }
    
    /**
     * Get all deletion requests with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminDeletionRequestResponse> getAllRequests(
            CustomerRequest.RequestStatus status, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerRequest> requestPage;
        
        if (status != null) {
            requestPage = requestRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            requestPage = requestRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        List<AdminDeletionRequestResponse> requests = requestPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminDeletionRequestResponse>builder()
                .content(requests)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(requestPage.getNumber())
                        .pageSize(requestPage.getSize())
                        .totalElements(requestPage.getTotalElements())
                        .totalPages(requestPage.getTotalPages())
                        .first(requestPage.isFirst())
                        .last(requestPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get deletion request detail
     */
    @Transactional(readOnly = true)
    public AdminDeletionRequestResponse getRequestDetail(Integer id) {
        CustomerRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deletion request not found with ID: " + id));
        return mapToResponse(request);
    }
    
    /**
     * Approve deletion request (set to COMPLETED)
     */
    public AdminDeletionRequestResponse approveRequest(Integer id) {
        CustomerRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deletion request not found with ID: " + id));
        
        request.setStatus(CustomerRequest.RequestStatus.COMPLETED);
        request.setProcessedAt(LocalDateTime.now());
        CustomerRequest updated = requestRepository.save(request);
        return mapToResponse(updated);
    }
    
    /**
     * Reject deletion request (set to CANCELLED)
     */
    public AdminDeletionRequestResponse rejectRequest(Integer id) {
        CustomerRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deletion request not found with ID: " + id));
        
        request.setStatus(CustomerRequest.RequestStatus.CANCELLED);
        request.setProcessedAt(LocalDateTime.now());
        CustomerRequest updated = requestRepository.save(request);
        return mapToResponse(updated);
    }
    
    /**
     * Get request stats
     */
    @Transactional(readOnly = true)
    public DeletionRequestStats getStats() {
        long pending = requestRepository.countByStatus(CustomerRequest.RequestStatus.PENDING);
        long verified = requestRepository.countByStatus(CustomerRequest.RequestStatus.VERIFIED);
        long completed = requestRepository.countByStatus(CustomerRequest.RequestStatus.COMPLETED);
        long cancelled = requestRepository.countByStatus(CustomerRequest.RequestStatus.CANCELLED);
        
        return DeletionRequestStats.builder()
                .pendingCount(pending)
                .verifiedCount(verified)
                .completedCount(completed)
                .cancelledCount(cancelled)
                .totalCount(pending + verified + completed + cancelled)
                .build();
    }
    
    // ========== Helper Methods ==========
    
    private AdminDeletionRequestResponse mapToResponse(CustomerRequest request) {
        return AdminDeletionRequestResponse.builder()
                .id(request.getId())
                .email(request.getEmail())
                .reason(request.getReason())
                .status(request.getStatus() != null ? request.getStatus().toString() : null)
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt().format(DATE_FORMATTER) : null)
                .processedAt(request.getProcessedAt() != null ? request.getProcessedAt().format(DATE_FORMATTER) : null)
                .build();
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class DeletionRequestStats {
        private Long totalCount;
        private Long pendingCount;
        private Long verifiedCount;
        private Long completedCount;
        private Long cancelledCount;
    }
}
