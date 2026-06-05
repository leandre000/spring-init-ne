package com.utility.billing.exception;

import com.utility.billing.common.ApiError;
import com.utility.billing.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fe) {
                        return ApiError.ofField(
                                fe.getField(),
                                fe.getDefaultMessage(),
                                fe.getRejectedValue(),
                                fe.getCode()
                        );
                    }
                    return toGlobalApiError(error);
                })
                .toList();

        log.warn("Validation failed for request [{}]: {} error(s)", request.getRequestURI(), errors.size());
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ApiError> errors = ex.getConstraintViolations().stream()
                .map(cv -> {
                    String path = cv.getPropertyPath().toString();
                    String field = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    return ApiError.ofField(field, cv.getMessage(), cv.getInvalidValue(), null);
                })
                .toList();

        log.warn("Constraint violation for request [{}]: {} error(s)", request.getRequestURI(), errors.size());
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(),
                List.of(ApiError.ofGlobal(ex.getMessage(), "NOT_FOUND")), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.CONFLICT, ex.getMessage(),
                List.of(ApiError.ofGlobal(ex.getMessage(), "ALREADY_EXISTS")), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized access [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(),
                List.of(ApiError.ofGlobal(ex.getMessage(), "UNAUTHORIZED")), request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(
            ForbiddenException ex, HttpServletRequest request) {
        log.warn("Forbidden access [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(),
                List.of(ApiError.ofGlobal(ex.getMessage(), "FORBIDDEN")), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(
            BusinessException ex, HttpServletRequest request) {
        log.warn("Business exception [{}] status={}: {}", request.getRequestURI(), ex.getStatus(), ex.getMessage());
        return buildError(ex.getStatus(), ex.getMessage(), List.of(ApiError.ofGlobal(ex.getMessage())), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.warn("Authentication failure [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, "Authentication required",
                List.of(ApiError.ofGlobal("Authentication required", "UNAUTHORIZED")), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.FORBIDDEN, "Access denied",
                List.of(ApiError.ofGlobal("You do not have permission to perform this action", "FORBIDDEN")), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation [{}]: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        String clientMessage = extractDuplicateFieldHint(ex);
        return buildError(HttpStatus.CONFLICT, clientMessage,
                List.of(ApiError.ofGlobal(clientMessage, "DATA_INTEGRITY_VIOLATION")), request);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleTransactionSystemException(
            TransactionSystemException ex, HttpServletRequest request) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException cve) {
            return handleConstraintViolation(cve, request);
        }
        log.error("Transaction system exception [{}]", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
                List.of(ApiError.ofGlobal("An unexpected error occurred. Please try again later.", "INTERNAL_ERROR")),
                request);
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleJpaSystemException(
            JpaSystemException ex, HttpServletRequest request) {
        Throwable cause = ex.getRootCause();
        if (cause instanceof ConstraintViolationException cve) {
            return handleConstraintViolation(cve, request);
        }
        log.error("JPA system exception [{}]", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
                List.of(ApiError.ofGlobal("An unexpected error occurred. Please try again later.", "INTERNAL_ERROR")),
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Unreadable request body [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body",
                List.of(ApiError.ofGlobal("Request body is missing or contains invalid JSON", "INVALID_REQUEST_BODY")),
                request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String message = "HTTP method '%s' is not supported for this endpoint".formatted(ex.getMethod());
        log.warn("Method not supported [{}]: {}", request.getRequestURI(), message);
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, message,
                List.of(ApiError.ofGlobal(message, "METHOD_NOT_ALLOWED")), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingRequestParam(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        String message = "Required parameter '%s' is missing".formatted(ex.getParameterName());
        log.warn("Missing request parameter [{}]: {}", request.getRequestURI(), message);
        return buildError(HttpStatus.BAD_REQUEST, message,
                List.of(ApiError.ofField(ex.getParameterName(), message, null, "MISSING_PARAMETER")), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message  = "Parameter '%s' must be of type %s".formatted(ex.getName(), expected);
        log.warn("Type mismatch [{}]: {}", request.getRequestURI(), message);
        return buildError(HttpStatus.BAD_REQUEST, message,
                List.of(ApiError.ofField(ex.getName(), message, ex.getValue(), "TYPE_MISMATCH")), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex, HttpServletRequest request) {
        String message = "No endpoint found for [%s] %s".formatted(ex.getHttpMethod(), request.getRequestURI());
        log.warn("No resource found: {}", message);
        return buildError(HttpStatus.NOT_FOUND, message,
                List.of(ApiError.ofGlobal(message, "ENDPOINT_NOT_FOUND")), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception for request [{}]", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred",
                List.of(ApiError.ofGlobal("An unexpected error occurred. Please try again later.", "INTERNAL_ERROR")),
                request);
    }

    private ResponseEntity<ApiResponse<Void>> buildError(
            HttpStatus status, String message, List<ApiError> errors, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(
                        ApiResponse.<Void>builder()
                                .success(false)
                                .message(message)
                                .errors(errors)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    private ApiError toGlobalApiError(ObjectError error) {
        return ApiError.builder()
                .message(error.getDefaultMessage())
                .code(error.getCode())
                .build();
    }

    private String extractDuplicateFieldHint(DataIntegrityViolationException ex) {
        String causeMessage = ex.getMostSpecificCause().getMessage();
        if (causeMessage != null) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("Key \\((.+?)\\)=\\((.+?)\\) already exists")
                    .matcher(causeMessage);
            if (matcher.find()) {
                String field = matcher.group(1);
                String value = matcher.group(2);
                return "%s '%s' is already in use".formatted(
                        Character.toUpperCase(field.charAt(0)) + field.substring(1),
                        value
                );
            }
            matcher = java.util.regex.Pattern
                    .compile("Duplicate entry '(.+?)' for key '(.+?)'")
                    .matcher(causeMessage);
            if (matcher.find()) {
                String value   = matcher.group(1);
                String keyName = matcher.group(2);
                String field = keyName.contains(".") ? keyName.substring(keyName.lastIndexOf('.') + 1) : keyName;
                return "%s '%s' is already in use".formatted(
                        Character.toUpperCase(field.charAt(0)) + field.substring(1),
                        value
                );
            }
        }
        return "A record with the provided details already exists";
    }
}
