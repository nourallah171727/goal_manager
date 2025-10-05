package com.tum.goal_manager.exception.exceptions;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;

public class SystemException extends ApiException {

    public SystemException(ErrorCode errorCode, String detail, Object... params) {
        super(errorCode, detail, params);
    }

    public SystemException(ErrorCode errorCode, String detail, Throwable cause, Object... params) {
        super(errorCode, detail, cause, params);
    }

    // Convenience constructors
    public static SystemException databaseError(String operation, Throwable cause) {
        return new SystemException(ErrorCode.DATABASE_ERROR,
                "Database operation failed: %s", cause, operation);
    }

    public static SystemException fileProcessingError(String filename, Throwable cause) {
        return new SystemException(ErrorCode.FILE_PROCESSING_ERROR,
                "Failed to process file: %s", cause, filename);
    }

    public static SystemException externalServiceError(String serviceName, Throwable cause) {
        return new SystemException(ErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
                "External service '%s' is unavailable", cause, serviceName);
    }
}