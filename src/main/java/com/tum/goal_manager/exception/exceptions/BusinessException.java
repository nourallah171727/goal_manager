package com.tum.goal_manager.exception.exceptions;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;

public class BusinessException extends ApiException {

    public BusinessException(ErrorCode errorCode, String detail, Object... params) {
        super(errorCode, detail, params);
    }

    // Convenience constructors
    public static BusinessException userAlreadyExists(String email) {
        return new BusinessException(ErrorCode.USER_ALREADY_EXISTS,
                "User with email '%s' already exists", email);
    }

    public static BusinessException goalAlreadyCompleted(Long goalId) {
        return new BusinessException(ErrorCode.GOAL_ALREADY_COMPLETED,
                "Goal with ID %d is already completed", goalId);
    }

    public static BusinessException operationNotAllowed(String operation, String currentState) {
        return new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED,
                "Operation '%s' not allowed in state: %s", operation, currentState);
    }
}