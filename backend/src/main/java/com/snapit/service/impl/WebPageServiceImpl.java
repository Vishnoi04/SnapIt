package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.service.SavedItemsService;
import com.snapit.service.WebPageExtractionService;
import com.snapit.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebPageServiceImpl implements WebPageService {

    @Autowired
    private WebPageExtractionService webPageExtractionService;
    @Autowired
    private SavedItemsService savedItemsService;

    @Override
    public SavedItemsResponse saveWebPage(String url) {
        String title = webPageExtractionService.extractTitle(url);
        String description = webPageExtractionService.extractDescription(url);
        String text = webPageExtractionService.extractText(url);
        SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
        savedItemsRequest.setDescription(description);
        savedItemsRequest.setTitle(title);
        savedItemsRequest.setExtractedText(text);
        savedItemsRequest.setContentType(MemoryType.WEBPAGE);
        savedItemsRequest.setSourceUrl(url);
        return savedItemsService.saveItem(savedItemsRequest);
    }
}
