package com.lanhcare.service;

import com.lanhcare.dto.subscription.SepayPurchaseResponse;
import com.lanhcare.dto.subscription.SepayWebhookDTO;

/**
 * SePay Payment Service
 * Handles creating payment info (QR code) and processing webhooks from SePay.
 */
public interface SepayService {

    /**
     * Create payment information for bank transfer via SePay.
     * Generates QR code URL with VietQR standard.
     *
     * @param transactionId Our internal transaction ID
     * @param amount        Amount in VND
     * @return SepayPurchaseResponse containing bank info and QR URL
     */
    SepayPurchaseResponse createPaymentInfo(Integer transactionId, long amount);

    /**
     * Verify the API Key sent by SePay in webhook requests.
     *
     * @param apiKey The API Key from the Authorization header
     * @return true if the API Key matches our configured key
     */
    boolean verifyWebhookApiKey(String apiKey);

    /**
     * Process incoming webhook from SePay.
     * Matches the transaction by content, verifies amount, and activates subscription.
     *
     * @param webhookData The webhook payload from SePay
     */
    void processWebhook(SepayWebhookDTO webhookData);
}
