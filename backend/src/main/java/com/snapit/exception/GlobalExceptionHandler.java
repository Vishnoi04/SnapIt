package com.snapit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(new ApiErrorResponse("Validation failed.", errors));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(Exception exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse("Request is malformed or contains an invalid value."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleUploadTooLarge(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ApiErrorResponse("Uploaded file exceeds the maximum allowed size."));
    }

    @ExceptionHandler(UploadTooLargeException.class)
    public ResponseEntity<ApiErrorResponse> handleUploadTooLarge(UploadTooLargeException exception) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiErrorResponse> handleFileStorageException(FileStorageException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("Unable to access the saved item file."));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(WebPageFetchException.class)
    public ResponseEntity<ApiErrorResponse> handleWebPageFetchException(WebPageFetchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PdfExtractionException.class)
    public ResponseEntity<ApiErrorResponse> handlePdfExtractionException(PdfExtractionException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ImageOcrException.class)
    public ResponseEntity<ApiErrorResponse> handleImageOcrException(ImageOcrException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidImageException(InvalidImageException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationCredentialsNotFoundException.class})
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(exception.getMessage()));
    }
}
