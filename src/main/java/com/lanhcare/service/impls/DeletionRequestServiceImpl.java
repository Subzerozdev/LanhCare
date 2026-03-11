package com.lanhcare.service.impls;

import com.lanhcare.dto.email.EmailRequest;
import com.lanhcare.entity.CustomerRequest;
import com.lanhcare.repository.CustomerRequestRepository;
import com.lanhcare.service.DeletionRequestService;
import com.lanhcare.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletionRequestServiceImpl implements DeletionRequestService {
    private final CustomerRequestRepository repository;
    private final EmailService emailService;

    @Override
    public CustomerRequest createRequest(String email, String reason) {
        String token = UUID.randomUUID().toString();

        CustomerRequest request = CustomerRequest.builder()
                .email(email)
                .reason(reason)
                .verificationCode(token)
                .status(CustomerRequest.RequestStatus.PENDING)
                .build();

        CustomerRequest saved = repository.save(request);

        // Gửi email xác nhận
        emailService.sendHtmlEmail(EmailRequest.builder()
                .to(email)
                .template("verify-email")
                .verifyToken(token)
                .attributes(new HashMap<>())
                .build());

        return saved;
    }

    @Override
    public CustomerRequest updateStatus(Integer id, CustomerRequest.RequestStatus status) {
        CustomerRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);

        if (status == CustomerRequest.RequestStatus.COMPLETED) {
            sendSuccessEmail(request.getEmail());
        }

        return repository.save(request);
    }

    @Override
    public List<CustomerRequest> getAllRequests() {
        return repository.findAll();
    }

    @Override
    public void verifyCode(String code) {
        // Tìm yêu cầu dựa trên mã xác nhận
        CustomerRequest request = repository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Mã xác nhận không hợp lệ hoặc đã hết hạn."));

        // Kiểm tra xem yêu cầu có đang ở trạng thái PENDING không
        if (request.getStatus() != CustomerRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Yêu cầu này đã được xử lý hoặc không còn hiệu lực.");
        }

        // Chuyển trạng thái sang VERIFIED
        request.setStatus(CustomerRequest.RequestStatus.VERIFIED);

        // Cập nhật thời điểm xử lý (tùy chọn: dùng để tính toán thời gian chờ xóa thực tế)
        request.setProcessedAt(LocalDateTime.now());

        repository.save(request);
    }

    private void sendSuccessEmail(String email) {
        emailService.sendHtmlEmail(EmailRequest.builder()
                .to(email)
                .verifyToken("SUCCESS_NOT_TOKEN")
                .template("deletion-success")
                .attributes(new HashMap<>())
                .build());
    }
}