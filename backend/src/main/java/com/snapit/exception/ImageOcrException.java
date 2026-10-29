package com.snapit.exception;

public class ImageOcrException extends RuntimeException{
    public ImageOcrException(String message, Throwable throwable){
        super(message, throwable);
    }
}
