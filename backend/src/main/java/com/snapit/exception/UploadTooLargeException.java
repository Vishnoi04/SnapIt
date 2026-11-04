package com.snapit.exception;

public class UploadTooLargeException extends RuntimeException {
    public UploadTooLargeException() {
        super("Uploaded file exceeds the maximum allowed size.");
    }
}
