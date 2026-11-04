package com.snapit.controller;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.service.FileStorageService;
import com.snapit.service.PdfExtractionService;
import com.snapit.service.SavedItemsService;
import com.snapit.exception.UploadTooLargeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdfs")
public class PdfController {
    @Autowired
    private SavedItemsService savedItemsService;
    @Autowired
    private PdfExtractionService pdfExtractionService;
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<SavedItemsResponse> uploadPdf(@RequestParam("file") MultipartFile file)
            throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file cannot be empty.");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new UploadTooLargeException();
        }
        String extractedText = pdfExtractionService.extractText(file);
        String filePath = fileStorageService.storeFile(file);
        Long savedItemId = null;
        try {
            SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
            savedItemsRequest.setTitle(resolveTitle(file.getOriginalFilename()));
            savedItemsRequest.setExtractedText(extractedText);
            savedItemsRequest.setContentType(MemoryType.PDF);
            savedItemsRequest.setDescription("PDF is uploaded on Snapit");
            savedItemsRequest.setFilePath(filePath);
            SavedItemsResponse savedItemsResponse = savedItemsService.saveItem(savedItemsRequest);
            savedItemId = savedItemsResponse.getId();
            savedItemsResponse.setProcessingStatus(ProcessingStatus.COMPLETED);
            savedItemsService.updateProcessingStatus(savedItemsResponse.getId(), ProcessingStatus.COMPLETED);
            return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
        } catch (RuntimeException exception) {
            cleanupFailedUpload(savedItemId, filePath, exception);
            throw exception;
        }
    }

    private String resolveTitle(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "Uploaded PDF";
        }
        return originalFilename.length() <= 200 ? originalFilename : originalFilename.substring(0, 200);
    }

    private void cleanupFailedUpload(Long savedItemId, String filePath, RuntimeException exception) {
        if (savedItemId != null) {
            try {
                savedItemsService.deleteSavedItemById(savedItemId);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
        }
        try {
            fileStorageService.deleteFile(filePath);
        } catch (IOException | RuntimeException cleanupException) {
            exception.addSuppressed(cleanupException);
        }
    }
}
