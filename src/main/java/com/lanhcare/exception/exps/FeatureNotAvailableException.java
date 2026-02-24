package com.lanhcare.exception.exps;

/**
 * Thrown when a user tries to access a feature that requires a higher subscription tier.
 */
public class FeatureNotAvailableException extends RuntimeException {

    private final String featureCode;

    public FeatureNotAvailableException(String featureCode) {
        super("Tính năng '" + featureCode + "' yêu cầu nâng cấp gói đăng ký.");
        this.featureCode = featureCode;
    }

    public FeatureNotAvailableException(String featureCode, String message) {
        super(message);
        this.featureCode = featureCode;
    }

    public String getFeatureCode() {
        return featureCode;
    }
}
