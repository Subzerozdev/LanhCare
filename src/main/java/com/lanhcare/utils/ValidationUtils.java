package com.lanhcare.utils;

import java.time.LocalDate;

public class ValidationUtils {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static LocalDate isValidDate(String date) {
        if (isNullOrEmpty(date)) return null;

        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValidIcdCode(String icdCode) {
        if (isNullOrEmpty(icdCode)) return false;

        if (icdCode.equals("unspecified")) return false;

        return !icdCode.equals("other");
    }
}
