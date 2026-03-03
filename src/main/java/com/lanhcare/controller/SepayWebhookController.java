package com.lanhcare.controller;

import com.lanhcare.dto.subscription.SepayWebhookDTO;
import com.lanhcare.service.SepayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SePay Webhook Controller (Public)
 * Receives webhook notifications from SePay when bank transactions occur.
 * This endpoint must be public (no JWT) because SePay calls it directly.
 */
@RestController
@RequestMapping("/api/public/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Callbacks", description = "SePay webhook endpoint (public)")
@Slf4j
public class SepayWebhookController {

    private final SepayService sepayService;

    /**
     * SePay Webhook - Receives bank transaction notifications.
     * SePay calls this endpoint when money is received in the linked bank account.
     * Authentication is via API Key in the Authorization header.
     *
     * @param authorization SePay API Key in format "Apikey {API_KEY}"
     * @param webhookData   Transaction data from SePay
     * @return success response
     */
    @PostMapping("/sepay-webhook")
    @Operation(summary = "SePay webhook callback",
               description = "Webhook endpoint for SePay to notify when bank transfer is received. Authenticated via API Key.")
    public ResponseEntity<Map<String, Object>> sepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody SepayWebhookDTO webhookData) {

        log.info("SePay webhook received - Authorization present: {}, Data: {}",
                authorization != null, webhookData);

        try {
            // 1. Verify API Key
            if (!sepayService.verifyWebhookApiKey(authorization)) {
                log.error("SePay webhook - Invalid API Key");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Invalid API Key"));
            }

            // 2. Process the webhook
            sepayService.processWebhook(webhookData);

            return ResponseEntity.ok(Map.of("success", true));

        } catch (Exception e) {
            log.error("SePay webhook processing error: {}", e.getMessage(), e);
            // Still return 200 to prevent SePay from retrying
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
