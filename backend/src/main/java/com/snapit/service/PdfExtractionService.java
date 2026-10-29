package com.snapit.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PdfExtractionService {
    String extractText(MultipartFile file);
}
