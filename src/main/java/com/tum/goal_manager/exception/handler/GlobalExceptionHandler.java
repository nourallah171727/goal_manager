package com.tum.goal_manager.exception.handler;

import com.tum.goal_manager.exception.ApiException;
import com.tum.goal_manager.exception.ErrorCode;
import com.tum.goal_manager.exception.response.ErrorResponse;
import com.tum.goal_manager.exception.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
/*
No need to declare Logger manually — @Slf4j does it for you.
The logger instance is called log by default.
You can use it immediately: log.info("Info message");
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== HANDLE CUSTOM API EXCEPTIONS =====

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        log.warn("Authentication exception: {}", e.getFullMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException e, HttpServletRequest request) {
        log.warn("Validation exception: {}", e.getFullMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.warn("Business exception: {}", e.getFullMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletRequest request) {
        log.warn("Resource not found: {}", e.getFullMessage());
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleSystemException(
            SystemException e, HttpServletRequest request) {
        log.error("System exception: {}", e.getFullMessage(), e);
        return buildErrorResponse(e, request);
    }

    // ===== HANDLE FRAMEWORK EXCEPTIONS =====

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        /*
        Triggered when Spring validation fails on @RequestBody or @Valid annotated DTOs.
Example: You have a UserDTO with:
@NotNull(message = "Name is required")
@Size(min = 3, max = 50)
private String name;
If the client sends an empty name, Spring throws MethodArgumentNotValidException.
         */
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INVALID_INPUT,
                request.getRequestURI(),
                "Validation failed for one or more fields",
                fieldErrors
        );

        log.warn("Validation failed: {}", fieldErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(
            Exception ex, HttpServletRequest request) {
        log.error("Uncaught exception: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_ERROR,
                request.getRequestURI(),
                "An unexpected error occurred"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ===== HELPER METHODS =====

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            ApiException exception, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.of(
                exception.getErrorCode(),
                request.getRequestURI(),
                exception.getMessage()
        );

        return new ResponseEntity<>(errorResponse, exception.getErrorCode().getHttpStatus());
    }
}