package com.snapit.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageOcrService {
    String extractText(MultipartFile file) throws IOException;
}
