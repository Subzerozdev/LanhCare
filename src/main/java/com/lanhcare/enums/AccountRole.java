package com.lanhcare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountRole {
    USER("ROLE_USER"),
    STAFF("ROLE_STAFF"),
    ADMIN("ROLE_ADMIN");

    private final String role;
}
