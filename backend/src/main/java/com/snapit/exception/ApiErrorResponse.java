package com.snapit.exception;

import java.util.Map;

public record ApiErrorResponse(String message, Map<String, String> fieldErrors) {
    public ApiErrorResponse(String message) {
        this(message, Map.of());
    }
}
