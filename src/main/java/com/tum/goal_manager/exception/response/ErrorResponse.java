package com.tum.goal_manager.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tum.goal_manager.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter  // Lombok - auto-generates getter methods for all fields
@Builder // Lombok - creates a builder pattern for easy object creation
@JsonInclude(JsonInclude.Include.NON_NULL) // Jackson - excludes null fields from JSON
public class ErrorResponse {
    private final LocalDateTime timestamp;  // When the error occurred
    private final String path;              // API endpoint where error happened
    private final String code;              // Your custom error code (e.g., "U001")
    private final String message;           // Human-readable error message
    private final String detail;            // Specific error details/context
    private final Integer status;           // HTTP status code (404, 500, etc.)
    private final Map<String, Object> metadata; // Additional error context

    public static ErrorResponse of(ErrorCode errorCode, String path, String detail) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(path)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .detail(detail)
                .status(errorCode.getHttpStatus().value())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String path, String detail,
                                   Map<String, Object> metadata) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(path)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .detail(detail)
                .status(errorCode.getHttpStatus().value())
                .metadata(metadata)
                .build();
    }

    /*
     Without factory method
ErrorResponse response = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .path("/api/users/123")
        .code(errorCode.getCode())
        .message(errorCode.getMessage())
        .detail("User not found")
        .status(errorCode.getHttpStatus().value())
        .build();

 With factory method
ErrorResponse response = ErrorResponse.of(errorCode, "/api/users/123", "User not found");
     */
}