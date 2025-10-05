package com.tum.goal_manager.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter //just to make getters be generated automatically
public enum ErrorCode {
    // ===== AUTHENTICATION & AUTHORIZATION (A-series) =====
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "A001", "Authentication required"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A002", "Invalid email or password"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "Access token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "Invalid access token"),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "A005", "Insufficient permissions"),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "A006", "Account is temporarily locked"),

    // ===== VALIDATION ERRORS (V-series) =====
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "V001", "Invalid input data"),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "V002", "Required field is missing"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "V003", "Invalid email format"),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "V004", "Invalid date format"),
    DATA_TOO_LONG(HttpStatus.BAD_REQUEST, "V005", "Data exceeds maximum length"),
    INVALID_RANGE(HttpStatus.BAD_REQUEST, "V006", "Invalid value range"),

    // ===== BUSINESS LOGIC ERRORS (B-series) =====
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "B001", "User already exists"),
    GOAL_ALREADY_COMPLETED(HttpStatus.CONFLICT, "B002", "Goal is already completed"),
    TASK_ALREADY_COMPLETED(HttpStatus.CONFLICT, "B003", "Task is already completed"),
    OPERATION_NOT_ALLOWED(HttpStatus.CONFLICT, "B004", "Operation not allowed in current state"),
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "B005", "Resource conflict detected"),
    BUSINESS_RULE_VIOLATION(HttpStatus.CONFLICT, "B006", "Business rule violation"),

    // ===== RESOURCE NOT FOUND (R-series) =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "User not found"),
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "R002", "Goal not found"),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "R003", "Task not found"),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "R004", "Feed not found"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "R005", "File not found"),

    // ===== SYSTEM & EXTERNAL ERRORS (S-series) =====
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "Internal server error"),
    EXTERNAL_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "S002", "External service unavailable"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "Database operation failed"),
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S004", "File processing failed"),
    NETWORK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S005", "Network communication error");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public boolean isClientError() {
        return httpStatus.is4xxClientError();
    }
/*
isClientError()
Returns true if the HTTP status code is in the 4xx range (400–499).
These are client errors, meaning the client did something wrong, e.g.
 */
    public boolean isServerError() {
        return httpStatus.is5xxServerError();
    }
    /*
    isServerError()
Returns true if the HTTP status code is in the 5xx range (500–599).
These are server errors, meaning the server failed to handle the request properly
     */
}