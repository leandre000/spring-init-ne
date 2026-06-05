package com.utility.billing.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final String field;
    private final String message;
    private final Object rejectedValue;
    private final String code;

    public static ApiError ofField(String field, String message, Object rejectedValue, String code) {
        return ApiError.builder()
                .field(field)
                .message(message)
                .rejectedValue(rejectedValue)
                .code(code)
                .build();
    }

    public static ApiError ofField(String field, String message) {
        return ApiError.builder()
                .field(field)
                .message(message)
                .build();
    }

    public static ApiError ofGlobal(String message, String code) {
        return ApiError.builder()
                .message(message)
                .code(code)
                .build();
    }

    public static ApiError ofGlobal(String message) {
        return ApiError.builder()
                .message(message)
                .build();
    }
}
