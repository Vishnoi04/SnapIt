package com.snapit.controller;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.service.FileStorageService;
import com.snapit.service.PdfExtractionService;
import com.snapit.service.SavedItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.management.MemoryUsage;

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
    public ResponseEntity<SavedItemsResponse> uploadPdf(@RequestParam("file")MultipartFile file) throws IOException {
        if (file.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > 10 * 1024 * 1024){
            return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
        }
        String extractedText = pdfExtractionService.extractText(file);
        String filePath = fileStorageService.storeFile(file);

        SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
        savedItemsRequest.setTitle(file.getOriginalFilename());
        savedItemsRequest.setExtractedText(extractedText);
        savedItemsRequest.setContentType(MemoryType.PDF);
        savedItemsRequest.setDescription("PDF is uploaded on Snapit");
        savedItemsRequest.setFilePath(filePath);
        SavedItemsResponse savedItemsResponse = savedItemsService.saveItem(savedItemsRequest);
        savedItemsResponse.setProcessingStatus(ProcessingStatus.COMPLETED);
        savedItemsService.updateProcessingStatus(savedItemsResponse.getId(), ProcessingStatus.COMPLETED);
        return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
    }
}
