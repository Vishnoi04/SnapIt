package com.snapit.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message, Throwable throwable){
        super(message, throwable);
    }
}
