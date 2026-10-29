package com.snapit.exception;

public class InvalidImageException extends RuntimeException{
    public InvalidImageException(String message, Throwable throwable){
        super(message, throwable);
    }
}
