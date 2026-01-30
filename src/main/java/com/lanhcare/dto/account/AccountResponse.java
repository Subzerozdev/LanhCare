package com.lanhcare.dto.account;

import com.lanhcare.enums.AccountRole;
import com.lanhcare.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Account response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Integer id;
    private String email;
    private String fullname;
    private AccountRole role;
    private AccountStatus status;
    private Boolean isHaveHealthProfile;
}
