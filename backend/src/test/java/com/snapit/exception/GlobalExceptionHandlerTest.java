package com.snapit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void validationErrorsUseConsistentEnvelopeAndIncludeFieldDetails() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "must be a valid email"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        new MethodParameter(getClass().getDeclaredMethod("requestMethod", String.class), 0),
                        bindingResult
                );

        var response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed.", response.getBody().message());
        assertEquals("must be a valid email", response.getBody().fieldErrors().get("email"));
    }

    @Test
    void nonValidationErrorsUseSameEnvelopeWithEmptyFieldErrors() {
        var response = exceptionHandler.handleResourceNotFoundException(
                new ResourceNotFoundException("Saved item not found.")
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Saved item not found.", response.getBody().message());
        assertTrue(response.getBody().fieldErrors().isEmpty());
    }

    @Test
    void malformedJsonUsesStandardErrorEnvelope() {
        var response = exceptionHandler.handleMalformedRequest(
                new HttpMessageNotReadableException("Malformed JSON", new IllegalArgumentException("Invalid body"))
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request is malformed or contains an invalid value.", response.getBody().message());
        assertTrue(response.getBody().fieldErrors().isEmpty());
    }

    private String requestMethod(String email) {
        return email;
    }
}
