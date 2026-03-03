package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.PurchaseResponse;
import com.lanhcare.dto.subscription.PurchaseSubscriptionRequest;
import com.lanhcare.dto.subscription.SepayPurchaseResponse;
import com.lanhcare.dto.subscription.SubscriptionResponse;
import com.lanhcare.dto.subscription.TransactionHistoryResponse;
import com.lanhcare.dto.subscription.TransactionStatusResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.ServicePlan;
import com.lanhcare.entity.Subscription;
import com.lanhcare.entity.Transaction;
import com.lanhcare.enums.ServicePlanStatus;
import com.lanhcare.enums.SubscriptionStatus;
import com.lanhcare.enums.TransactionStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.ServicePlanRepository;
import com.lanhcare.repository.SubscriptionRepository;
import com.lanhcare.repository.TransactionRepository;
import com.lanhcare.service.SepayService;
import com.lanhcare.service.SubscriptionService;
import com.lanhcare.service.VNPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of SubscriptionService
 * Handles subscription purchase, cancellation, and feature checking
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ServicePlanRepository servicePlanRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final VNPayService vnPayService;
    private final SepayService sepayService;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Default features for Free users (no active subscription)
    private static final String FREE_FEATURES = "DAILY_LOG_LIMITED,FORUM_VIEW,HOSPITAL_SEARCH";

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getMySubscription(Integer accountId) {
        Optional<Subscription> activeSub = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);

        if (activeSub.isEmpty()) {
            return null; // No active subscription - user is on Free plan
        }

        Subscription sub = activeSub.get();

        // Check if subscription has expired
        if (sub.getEndDate().isBefore(LocalDateTime.now())) {
            sub.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepository.save(sub);
            return null; // Expired - user is back on Free plan
        }

        return mapToSubscriptionResponse(sub);
    }

    @Override
    public PurchaseResponse purchaseSubscription(Integer accountId, PurchaseSubscriptionRequest request, String ipAddress) {
        // 1. Check if user already has an active subscription
        Optional<Subscription> existingSub = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);

        if (existingSub.isPresent()) {
            Subscription sub = existingSub.get();
            // Check if it's not expired
            if (sub.getEndDate().isAfter(LocalDateTime.now())) {
                if (sub.getServicePlan().getId().equals(request.getServicePlanId())) {
                    throw new IllegalStateException("Bạn đã đăng ký gói này rồi. Gói hiện tại còn hiệu lực đến " 
                            + sub.getEndDate().format(DATE_FORMAT));
                }
                // Cancel current subscription if upgrading/downgrading
                sub.setStatus(SubscriptionStatus.CANCELLED);
                subscriptionRepository.save(sub);
                log.info("Cancelled existing subscription {} for account {} during upgrade", sub.getId(), accountId);
            } else {
                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
            }
        }

        // 2. Get the service plan
        ServicePlan plan = servicePlanRepository.findById(request.getServicePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói dịch vụ với ID: " + request.getServicePlanId()));

        if (plan.getStatus() != ServicePlanStatus.ACTIVE) {
            throw new IllegalStateException("Gói dịch vụ này hiện không khả dụng");
        }

        // 3. Get the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // 4. Create Transaction (PENDING)
        Transaction transaction = Transaction.builder()
                .account(account)
                .servicePlan(plan)
                .amount(plan.getPrice())
                .paymentMethod("VNPAY")
                .status(TransactionStatus.PENDING)
                .build();
        transaction = transactionRepository.save(transaction);

        log.info("Created pending transaction {} for account {} purchasing plan {}", 
                transaction.getId(), accountId, plan.getName());

        // 5. Create VNPay payment URL
        String orderInfo = "Mua goi " + plan.getName() + " LanhCare";
        String paymentUrl = vnPayService.createPaymentUrl(
                transaction.getId(),
                plan.getPrice().longValue(),
                orderInfo,
                ipAddress
        );

        // 6. Return payment URL
        return PurchaseResponse.builder()
                .transactionId(transaction.getId())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public SepayPurchaseResponse purchaseSubscriptionSepay(Integer accountId, PurchaseSubscriptionRequest request) {
        // 1. Check if user already has an active subscription (same logic as VNPay)
        Optional<Subscription> existingSub = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);

        if (existingSub.isPresent()) {
            Subscription sub = existingSub.get();
            if (sub.getEndDate().isAfter(LocalDateTime.now())) {
                if (sub.getServicePlan().getId().equals(request.getServicePlanId())) {
                    throw new IllegalStateException("Bạn đã đăng ký gói này rồi. Gói hiện tại còn hiệu lực đến " 
                            + sub.getEndDate().format(DATE_FORMAT));
                }
                sub.setStatus(SubscriptionStatus.CANCELLED);
                subscriptionRepository.save(sub);
                log.info("Cancelled existing subscription {} for account {} during SePay upgrade", sub.getId(), accountId);
            } else {
                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
            }
        }

        // 2. Get the service plan
        ServicePlan plan = servicePlanRepository.findById(request.getServicePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói dịch vụ với ID: " + request.getServicePlanId()));

        if (plan.getStatus() != ServicePlanStatus.ACTIVE) {
            throw new IllegalStateException("Gói dịch vụ này hiện không khả dụng");
        }

        // 3. Get the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // 4. Create Transaction (PENDING) with SEPAY payment method
        Transaction transaction = Transaction.builder()
                .account(account)
                .servicePlan(plan)
                .amount(plan.getPrice())
                .paymentMethod("SEPAY")
                .status(TransactionStatus.PENDING)
                .build();
        transaction = transactionRepository.save(transaction);

        log.info("Created SePay pending transaction {} for account {} purchasing plan {}", 
                transaction.getId(), accountId, plan.getName());

        // 5. Create payment info with QR code
        return sepayService.createPaymentInfo(transaction.getId(), plan.getPrice().longValue());
    }

    @Override
    public void cancelSubscription(Integer accountId) {
        Subscription sub = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có gói dịch vụ nào đang hoạt động"));

        sub.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(sub);

        log.info("Cancelled subscription {} for account {}", sub.getId(), accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getMyTransactions(Integer accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByIdDesc(accountId);

        return transactions.stream()
                .map(this::mapToTransactionHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void processVNPayCallback(Map<String, String> params) {
        // 1. Verify signature
        if (!vnPayService.verifySignature(params)) {
            log.error("Invalid VNPay signature for callback");
            throw new SecurityException("Invalid VNPay signature");
        }

        // 2. Get transaction reference
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        log.info("VNPay callback - TxnRef: {}, ResponseCode: {}, TransactionStatus: {}", 
                txnRef, responseCode, transactionStatus);

        // 3. Find transaction
        Integer transactionId = Integer.parseInt(txnRef);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + txnRef));

        // 4. Check if already processed
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            log.info("Transaction {} already processed with status {}", txnRef, transaction.getStatus());
            return; // Already processed
        }

        // 5. Process based on response code
        if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
            // Payment successful
            transaction.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(transaction);

            // Create Subscription
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

            log.info("Subscription created for account {} - Plan: {}, Expires: {}", 
                    transaction.getAccount().getId(), plan.getName(), endDate);
        } else {
            // Payment failed
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            log.info("Transaction {} failed with response code: {}", txnRef, responseCode);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasFeature(Integer accountId, String featureCode) {
        Optional<Subscription> activeSub = subscriptionRepository
                .findByAccountIdAndStatus(accountId, SubscriptionStatus.ACTIVE);

        String features;

        if (activeSub.isEmpty()) {
            // No active subscription - use Free features
            features = FREE_FEATURES;
        } else {
            Subscription sub = activeSub.get();
            // Check if expired
            if (sub.getEndDate().isBefore(LocalDateTime.now())) {
                features = FREE_FEATURES;
            } else {
                features = sub.getServicePlan().getFeatures();
                if (features == null || features.isEmpty()) {
                    features = FREE_FEATURES;
                }
            }
        }

        return features.contains(featureCode);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionStatusResponse getTransactionStatus(Integer transactionId, Integer accountId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + transactionId));

        // Verify transaction belongs to this account
        if (!transaction.getAccount().getId().equals(accountId)) {
            throw new SecurityException("Bạn không có quyền xem giao dịch này");
        }

        // Build response
        TransactionStatusResponse.TransactionStatusResponseBuilder builder = TransactionStatusResponse.builder()
                .transactionId(transaction.getId())
                .status(transaction.getStatus().name())
                .planName(transaction.getServicePlan().getName())
                .planId(transaction.getServicePlan().getId());

        // Set message based on status
        switch (transaction.getStatus()) {
            case PENDING -> builder.message("Đang chờ thanh toán");
            case COMPLETED -> {
                builder.message("Thanh toán thành công");
                // Find the subscription created from this transaction
                subscriptionRepository.findByTransactionId(transactionId)
                        .ifPresent(sub -> builder.subscriptionId(sub.getId()));
            }
            case FAILED -> builder.message("Thanh toán thất bại");
        }

        return builder.build();
    }

    // ========== Private Helper Methods ==========

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

    private SubscriptionResponse mapToSubscriptionResponse(Subscription sub) {
        List<String> featureList = new ArrayList<>();
        if (sub.getServicePlan().getFeatures() != null && !sub.getServicePlan().getFeatures().isEmpty()) {
            featureList = Arrays.asList(sub.getServicePlan().getFeatures().split(","));
        }

        return SubscriptionResponse.builder()
                .id(sub.getId())
                .planId(sub.getServicePlan().getId())
                .planName(sub.getServicePlan().getName())
                .planDescription(sub.getServicePlan().getDescription())
                .planPrice(sub.getServicePlan().getPrice())
                .features(featureList)
                .startDate(sub.getStartDate().format(DATE_FORMAT))
                .endDate(sub.getEndDate().format(DATE_FORMAT))
                .status(sub.getStatus().name())
                .build();
    }

    private TransactionHistoryResponse mapToTransactionHistoryResponse(Transaction txn) {
        return TransactionHistoryResponse.builder()
                .id(txn.getId())
                .planName(txn.getServicePlan().getName())
                .amount(txn.getAmount())
                .paymentMethod(txn.getPaymentMethod())
                .status(txn.getStatus().name())
                .transactionDate(txn.getTransactionDate() != null 
                        ? txn.getTransactionDate().format(DATE_FORMAT) : null)
                .build();
    }
}
