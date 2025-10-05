package com.tum.goal_manager.exception;

import lombok.Getter;
import java.util.Arrays;

@Getter
public abstract class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String detail;      // The template string with %s, %d etc.
    private final Object[] params;    // Values to replace the placeholders

    protected ApiException(ErrorCode errorCode, String detail, Object... params) {
        super(generateMessage(detail, params));  // ✅ Pass params here!
        this.errorCode = errorCode;
        this.detail = detail;
        this.params = params != null ? params : new Object[0];
    }

    protected ApiException(ErrorCode errorCode, String detail, Throwable cause, Object... params) {
        super(generateMessage(detail, params), cause);  // ✅ Pass params here!
        this.errorCode = errorCode;
        this.detail = detail;
        this.params = params != null ? params : new Object[0];
    }

    // ✅ FIXED: Now uses params to format the detail string
    private static String generateMessage(String detail, Object[] params) {
        if (detail == null) return null;

        try {
            // Use String.format to replace %s, %d etc. with actual values
            return String.format(detail, params);
        } catch (Exception e) {
            // Fallback if formatting fails (wrong number of params, etc.)
            return detail + " [Params: " + Arrays.toString(params) + "]";
        }
    }

    public String getFullMessage() {
        return String.format("[%s] %s: %s",
                errorCode.getCode(),
                errorCode.getMessage(),
                getMessage());
    }
}