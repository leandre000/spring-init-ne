package com.utility.billing.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class ResponseBuilder {

    private ResponseBuilder() {}

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message("Operation successful")
                        .data(data)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message, HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static <T> ResponseEntity<ApiResponse<PagedResponse<T>>> ok(
            Page<T> page, String message, HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<T>>builder()
                        .success(true)
                        .message(message)
                        .data(PagedResponse.of(page))
                        .path(request.getRequestURI())
                        .build()
        );
    }

    public static <T, S> ResponseEntity<ApiResponse<PagedResponse<T>>> ok(
            List<T> mappedContent, Page<S> sourcePage, String message, HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.<PagedResponse<T>>builder()
                        .success(true)
                        .message(message)
                        .data(PagedResponse.of(mappedContent, sourcePage))
                        .path(request.getRequestURI())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(
            String message, List<ApiError> errors, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errors(errors)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(
            HttpStatus status, T data, String message, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(
                        ApiResponse.<T>builder()
                                .success(status.is2xxSuccessful())
                                .message(message)
                                .data(data)
                                .path(request.getRequestURI())
                                .build()
                );
    }

    public static <T> ResponseEntity<ApiResponse<T>> of(
            HttpStatus status, String message, List<ApiError> errors, HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(
                        ApiResponse.<T>builder()
                                .success(false)
                                .message(message)
                                .errors(errors)
                                .path(request.getRequestURI())
                                .build()
                );
    }
}
