package com.snapit.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;

    Resource loadFile(String filePath) throws IOException;

    void deleteFile(String filePath) throws IOException;
}
