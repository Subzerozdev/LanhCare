package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for SePay purchase.
 * Contains bank transfer info and QR code URL for the user to scan and pay.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SepayPurchaseResponse {

    /** Transaction ID in our system */
    private Integer transactionId;

    /** Bank name (e.g., "VietinBank") */
    private String bankName;

    /** Bank account number to transfer to */
    private String accountNumber;

    /** Account holder name */
    private String accountHolder;

    /** Amount to transfer (VND) */
    private BigDecimal amount;

    /** Transfer content - user MUST include this in transfer description */
    private String content;

    /** VietQR code image URL - user scans this with banking app */
    private String qrCodeUrl;
}
