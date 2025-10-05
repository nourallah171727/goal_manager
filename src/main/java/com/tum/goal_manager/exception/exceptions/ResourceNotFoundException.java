package com.tum.goal_manager.exception.exceptions;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException(ErrorCode errorCode, String detail, Object... params) {
        super(errorCode, detail, params);
    }

    // Convenience constructors
    public static ResourceNotFoundException userNotFound(Long userId) {
        return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                "User with ID %d not found", userId);
    }

    public static ResourceNotFoundException userNotFound(String email) {
        return new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                "User with email '%s' not found", email);
    }

    public static ResourceNotFoundException goalNotFound(Long goalId) {
        return new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                "Goal with ID %d not found", goalId);
    }

    public static ResourceNotFoundException taskNotFound(Long taskId) {
        return new ResourceNotFoundException(ErrorCode.TASK_NOT_FOUND,
                "Task with ID %d not found", taskId);
    }
}