package com.snapit.service.impl;

import com.snapit.service.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path storageDirectory = Path.of("./uploads").toAbsolutePath().normalize();
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".pdf");

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        Files.createDirectories(storageDirectory);
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            String candidate = fileName.substring(fileName.lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(candidate)) {
                extension = candidate;
            }
        }
        String storageFileName = UUID.randomUUID() + extension;
        Path targetLocation = storageDirectory.resolve(storageFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(targetLocation);
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw exception;
        }
        return targetLocation.toString();
    }

    @Override
    public Resource loadFile(String filePath) throws IOException {
        Path target = resolveStoredFile(filePath);
        if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new java.nio.file.NoSuchFileException(target.toString());
        }
        return new FileSystemResource(target);
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(resolveStoredFile(filePath));
    }

    private Path resolveStoredFile(String filePath) {
        Path target = Path.of(filePath).toAbsolutePath().normalize();
        if (!target.startsWith(storageDirectory)
                || !storageDirectory.equals(target.getParent())
                || target.equals(storageDirectory)) {
            throw new IllegalArgumentException("File path is outside the configured storage directory.");
        }
        return target;
    }
}
