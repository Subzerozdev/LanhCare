package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for receiving SePay webhook notifications.
 * SePay sends POST request with this JSON body when a bank transaction occurs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SepayWebhookDTO {

    /** SePay internal transaction ID */
    private Long id;

    /** Bank gateway name (e.g., "VietinBank", "BIDV") */
    private String gateway;

    /** Transaction date from bank (format: "yyyy-MM-dd HH:mm:ss") */
    private String transactionDate;

    /** Bank account number that received the money */
    private String accountNumber;

    /** Sub-account number (virtual account, if configured) */
    private String subAccount;

    /** Transfer type: "in" = money received, "out" = money sent */
    private String transferType;

    /** Transfer amount in VND (integer, e.g., 99000) */
    private Long transferAmount;

    /** Transfer content/description - contains our transaction code (e.g., "LC42") */
    private String content;

    /** Bank reference code */
    private String referenceCode;

    /** Full transaction description from bank */
    private String description;
}
