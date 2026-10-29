package com.snapit.exception;

public class PdfExtractionException extends RuntimeException{
    public PdfExtractionException(String message, Throwable throwable){
        super(message, throwable);
    }
}
