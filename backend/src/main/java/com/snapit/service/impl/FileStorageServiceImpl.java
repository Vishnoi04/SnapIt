package com.snapit.service.impl;

import com.snapit.service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

public class FileStorageServiceImpl implements FileStorageService {
    private final Path storageDirectory = Path.of("./uploads").toAbsolutePath().normalize();
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        Files.createDirectories(storageDirectory);
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")){
           extension = fileName.substring(fileName.lastIndexOf(".")) .toLowerCase(Locale.ROOT);
            if (extension.length() > 10) {
                extension = "";
            }
        }
        String storageFileName = UUID.randomUUID() + extension;
        Path targetLocation = storageDirectory.resolve(storageFileName);
        Files.copy(
                file.getInputStream(),
                targetLocation,
                StandardCopyOption.REPLACE_EXISTING
        );
        return targetLocation.toString();
    }
}
