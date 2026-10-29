package com.snapit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.management.monitor.StringMonitor;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException exception){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message : ", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WebPageFetchException.class)
    public ResponseEntity<Map<String, String>> handleWebPageFetchException(WebPageFetchException exception){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message : ", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(PdfExtractionException.class)
    public ResponseEntity<Map<String, String>> handlePdfExtractionException(PdfExtractionException exception){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message : ", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageOcrException.class)
    public ResponseEntity<Map<String, String>> handleImageOcrException(ImageOcrException exception){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message : ", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<Map<String, String>> handleInvalidImageException(InvalidImageException exception){
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message : ", exception.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }
}
