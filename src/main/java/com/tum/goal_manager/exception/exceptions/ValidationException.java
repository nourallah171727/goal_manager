package com.tum.goal_manager.exception.exceptions;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;

public class ValidationException extends ApiException {

    public ValidationException(ErrorCode errorCode, String detail, Object... params) {
        super(errorCode, detail, params);
    }

    // Convenience constructors
    public static ValidationException invalidInput(String field, String reason) {
        return new ValidationException(ErrorCode.INVALID_INPUT,
                "Field '%s': %s", field, reason);
    }

    public static ValidationException missingField(String fieldName) {
        return new ValidationException(ErrorCode.MISSING_REQUIRED_FIELD,
                "Required field '%s' is missing", fieldName);
    }

    public static ValidationException invalidEmail(String email) {
        return new ValidationException(ErrorCode.INVALID_EMAIL_FORMAT,
                "Invalid email format: %s", email);
    }

    public static ValidationException valueTooLong(String field, int maxLength) {
        return new ValidationException(ErrorCode.DATA_TOO_LONG,
                "Field '%s' exceeds maximum length of %d characters", field, maxLength);
    }
}