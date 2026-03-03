package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.SepayPurchaseResponse;
import com.lanhcare.dto.subscription.SepayWebhookDTO;
import com.lanhcare.entity.ServicePlan;
import com.lanhcare.entity.Subscription;
import com.lanhcare.entity.Transaction;
import com.lanhcare.enums.SubscriptionStatus;
import com.lanhcare.enums.TransactionStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.SubscriptionRepository;
import com.lanhcare.repository.TransactionRepository;
import com.lanhcare.service.SepayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * SePay Payment Service Implementation
 * Handles QR code generation and webhook processing for bank transfer payments.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SepayServiceImpl implements SepayService {

    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${sepay.api-key}")
    private String sepayApiKey;

    @Value("${sepay.bank-name}")
    private String bankName;

    @Value("${sepay.account-number}")
    private String accountNumber;

    @Value("${sepay.account-holder}")
    private String accountHolder;

    @Value("${sepay.prefix:LC}")
    private String transactionPrefix;

    @Override
    public SepayPurchaseResponse createPaymentInfo(Integer transactionId, long amount) {
        // Generate unique transfer content using prefix + transactionId
        String content = transactionPrefix + transactionId;

        // Build VietQR URL via SePay
        String qrCodeUrl = String.format(
                "https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%d&des=%s",
                accountNumber,
                URLEncoder.encode(bankName, StandardCharsets.UTF_8),
                amount,
                URLEncoder.encode(content, StandardCharsets.UTF_8)
        );

        log.info("Created SePay payment info - TransactionId: {}, Content: {}, Amount: {}, QR: {}",
                transactionId, content, amount, qrCodeUrl);

        return SepayPurchaseResponse.builder()
                .transactionId(transactionId)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .accountHolder(accountHolder)
                .amount(BigDecimal.valueOf(amount))
                .content(content)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }

    @Override
    public boolean verifyWebhookApiKey(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return false;
        }

        // SePay sends: "Apikey {API_KEY}"
        String apiKey = authorizationHeader.replace("Apikey ", "").trim();
        return sepayApiKey.equals(apiKey);
    }

    @Override
    @Transactional
    public void processWebhook(SepayWebhookDTO webhookData) {
        log.info("Processing SePay webhook - ID: {}, Amount: {}, Content: '{}', Gateway: {}",
                webhookData.getId(), webhookData.getTransferAmount(),
                webhookData.getContent(), webhookData.getGateway());

        // 1. Only process incoming transfers
        if (!"in".equalsIgnoreCase(webhookData.getTransferType())) {
            log.info("Skipping non-incoming transfer type: {}", webhookData.getTransferType());
            return;
        }

        // 2. Extract transaction ID from content (e.g., "LC42" -> 42)
        Integer transactionId = extractTransactionId(webhookData.getContent());
        if (transactionId == null) {
            log.warn("Could not extract transaction ID from content: '{}'", webhookData.getContent());
            return;
        }

        // 3. Find the transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found for SePay webhook: " + transactionId));

        // 4. Check if already processed
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transaction {} already processed with status: {}", transactionId, transaction.getStatus());
            return;
        }

        // 5. Verify payment method
        if (!"SEPAY".equals(transaction.getPaymentMethod())) {
            log.warn("Transaction {} has payment method '{}', expected 'SEPAY'",
                    transactionId, transaction.getPaymentMethod());
            return;
        }

        // 6. Verify amount matches
        long expectedAmount = transaction.getAmount().longValue();
        long receivedAmount = webhookData.getTransferAmount();
        if (receivedAmount < expectedAmount) {
            log.warn("Amount mismatch for transaction {}: expected={}, received={}",
                    transactionId, expectedAmount, receivedAmount);
            return;
        }

        // 7. Mark transaction as completed
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        // 8. Create Subscription
        ServicePlan plan = transaction.getServicePlan();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan);

        Subscription subscription = Subscription.builder()
                .account(transaction.getAccount())
                .servicePlan(plan)
                .transaction(transaction)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscriptionRepository.save(subscription);

        log.info("SePay payment successful! Transaction: {}, Account: {}, Plan: {}, Expires: {}",
                transactionId, transaction.getAccount().getId(), plan.getName(), endDate);
    }

    /**
     * Extract transaction ID from transfer content.
     * Content format: "{prefix}{transactionId}" (e.g., "LC42")
     * SePay may append extra text, so we search for the prefix pattern.
     */
    private Integer extractTransactionId(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        try {
            // Convert to uppercase for case-insensitive matching
            String upperContent = content.toUpperCase().trim();
            String upperPrefix = transactionPrefix.toUpperCase();

            // Find the prefix in the content
            int prefixIndex = upperContent.indexOf(upperPrefix);
            if (prefixIndex == -1) {
                return null;
            }

            // Extract the number after the prefix
            String afterPrefix = upperContent.substring(prefixIndex + upperPrefix.length());

            // Extract consecutive digits
            StringBuilder digits = new StringBuilder();
            for (char c : afterPrefix.toCharArray()) {
                if (Character.isDigit(c)) {
                    digits.append(c);
                } else {
                    break; // Stop at first non-digit
                }
            }

            if (digits.isEmpty()) {
                return null;
            }

            return Integer.parseInt(digits.toString());
        } catch (NumberFormatException e) {
            log.error("Failed to parse transaction ID from content: '{}'", content, e);
            return null;
        }
    }

    /**
     * Calculate subscription end date based on service plan period.
     * Reuses the same logic as VNPay flow.
     */
    private LocalDateTime calculateEndDate(LocalDateTime startDate, ServicePlan plan) {
        if (plan.getPeriodValue() == null || plan.getPeriodUnit() == null) {
            return startDate.plusMonths(1); // Default: 1 month
        }

        return switch (plan.getPeriodUnit()) {
            case DAY -> startDate.plusDays(plan.getPeriodValue());
            case WEEK -> startDate.plusWeeks(plan.getPeriodValue());
            case MONTH -> startDate.plusMonths(plan.getPeriodValue());
            case YEAR -> startDate.plusYears(plan.getPeriodValue());
        };
    }
}
