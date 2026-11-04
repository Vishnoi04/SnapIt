package com.snapit.service.impl;

import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStorageServiceImplTest {
    private final FileStorageServiceImpl fileStorageService = new FileStorageServiceImpl();

    @Test
    void refusesToReadOrDeletePathsOutsideStorageDirectory() {
        String outsidePath = Path.of("..", "outside", "secret.pdf").toString();

        assertThrows(IllegalArgumentException.class, () -> fileStorageService.loadFile(outsidePath));
        assertThrows(IllegalArgumentException.class, () -> fileStorageService.deleteFile(outsidePath));
    }

    @Test
    void refusesToLoadMissingStoredFiles() {
        String missingFile = Path.of("./uploads", "missing-test-file.pdf").toAbsolutePath().normalize().toString();

        assertThrows(NoSuchFileException.class, () -> fileStorageService.loadFile(missingFile));
    }
}
