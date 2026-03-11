package com.lanhcare.service.impls;

import com.lanhcare.dto.email.EmailRequest;
import com.lanhcare.service.EmailService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${mail.verify-url}")
    private String verifyTokenApi;

    @Value("${mail.api.key}")
    private String senderApiKey;

    @Value("${mail.from}")
    private String emailFrom;

    private final SpringTemplateEngine templateEngine;

    @Async
    @Override
    public void sendHtmlEmail(EmailRequest request) {
        try {
            // Xử lý Thymeleaf context
            Context context = getContext(request);
            String htmlContent = templateEngine.process(request.getTemplate(), context);

            // Khởi tạo Resend client
            Resend resend = new Resend(senderApiKey);

            // Cấu hình email options
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(emailFrom)
                    .to(request.getTo())
                    .subject("Notify User")
                    .html(htmlContent)
                    .build();

            // Gửi email
            CreateEmailResponse response = resend.emails().send(params);

            // Log ID để kiểm tra nếu cần
            System.out.println("Email sent successfully, ID: " + response.getId());

        } catch (ResendException e) {
            throw new RuntimeException("Resend API error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    private Context getContext(EmailRequest request) {
        String verifyUrl = verifyTokenApi + request.getVerifyToken();

        Context context = new Context();
        if (request.getAttributes() != null) {
            for (Map.Entry<String, String> entry : request.getAttributes().entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }

        if ("verify-email".equals(request.getTemplate())) {
            context.setVariable("verifyUrl", verifyUrl);
        }
        return context;
    }
}
