package com.lanhcare.controller;

import com.lanhcare.entity.CustomerRequest;
import com.lanhcare.service.DeletionRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deletion-requests")
@RequiredArgsConstructor
@Tag(name = "Admin/User - Deletion Request", description = "APIs for managing account deletion")
public class DeletionRequestController {
    private final DeletionRequestService deletionService;

    @PostMapping
    public ResponseEntity<CustomerRequest> create(@Valid @RequestBody com.lanhcare.dto.deleterequests.CustomerRequest request) {
        return ResponseEntity.ok(deletionService.createRequest(request.getEmail(), request.getReason()));
    }

    @GetMapping
    public ResponseEntity<List<CustomerRequest>> getAll() {
        return ResponseEntity.ok(deletionService.getAllRequests());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerRequest> updateStatus(
            @PathVariable Integer id,
            @RequestParam CustomerRequest.RequestStatus status
    ) {
        return ResponseEntity.ok(deletionService.updateStatus(id, status));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("code") String code) {
        try {
            deletionService.verifyCode(code);
            return ResponseEntity.ok("Xác thực thành công. Tài khoản của bạn đã được đưa vào hàng đợi xóa.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}