package com.snapit.controller;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.service.FileStorageService;
import com.snapit.service.ImageOcrService;
import com.snapit.service.SavedItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private SavedItemsService savedItemsService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ImageOcrService imageOcrService;

    @PostMapping
    public ResponseEntity<SavedItemsResponse> uploadImage(@RequestParam("file")MultipartFile file) throws IOException{
        if (file.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") || contentType.equals("image/jpeg"))){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (file.getSize() > 10 * 1024 * 1024){
            return new ResponseEntity<>(HttpStatus.PAYLOAD_TOO_LARGE);
        }

        String filePath = fileStorageService.storeFile(file);
        String extractText = imageOcrService.extractText(file);
        SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
        savedItemsRequest.setFilePath(filePath);
        savedItemsRequest.setDescription("Image uploaded on Snapit");
        savedItemsRequest.setTitle(file.getOriginalFilename());
        savedItemsRequest.setContentType(MemoryType.IMGAGE);
        savedItemsRequest.setExtractedText(extractText);

        SavedItemsResponse savedItemsResponse = savedItemsService.saveItem(savedItemsRequest);
        savedItemsResponse.setProcessingStatus(ProcessingStatus.COMPLETED);
        savedItemsService.updateProcessingStatus(savedItemsResponse.getId(), ProcessingStatus.COMPLETED);
        return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
    }
}
