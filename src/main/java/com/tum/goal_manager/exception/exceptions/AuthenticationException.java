package com.tum.goal_manager.exception.exceptions;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;

public class AuthenticationException extends ApiException {

    public AuthenticationException(ErrorCode errorCode, String detail, Object... params) {
        super(errorCode, detail, params);
    }

    public AuthenticationException(ErrorCode errorCode, String detail, Throwable cause, Object... params) {
        super(errorCode, detail, cause, params);
    }

    // Convenience constructors
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password provided");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException(ErrorCode.TOKEN_EXPIRED, "Authentication token has expired");
    }

    public static AuthenticationException insufficientPermissions(String requiredRole) {
        return new AuthenticationException(ErrorCode.INSUFFICIENT_PERMISSIONS,
                "Required role: " + requiredRole);
    }
}