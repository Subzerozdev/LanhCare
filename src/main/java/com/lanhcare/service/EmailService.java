package com.lanhcare.service;

import com.lanhcare.dto.email.EmailRequest;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {
    @Async
    void sendHtmlEmail(EmailRequest request);
}
