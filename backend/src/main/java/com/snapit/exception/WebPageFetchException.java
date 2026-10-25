package com.snapit.exception;

public class WebPageFetchException extends RuntimeException{
    public WebPageFetchException(String message, Throwable cause){
        super(message, cause);
    }
}
