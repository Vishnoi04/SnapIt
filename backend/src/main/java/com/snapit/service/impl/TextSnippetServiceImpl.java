package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.request.TextSnippetRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.service.SavedItemsService;
import com.snapit.service.TextSnippetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextSnippetServiceImpl implements TextSnippetService {
    @Autowired
    private SavedItemsService savedItemsService;

    @Override
    public SavedItemsResponse saveTextSnippet(TextSnippetRequest textSnippetRequest) {
        SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
        savedItemsRequest.setTitle(textSnippetRequest.getTitle());
        savedItemsRequest.setDescription(textSnippetRequest.getDescription());
        savedItemsRequest.setContentType(MemoryType.TEXT);
        savedItemsRequest.setLanguage(textSnippetRequest.getLanguage());
        savedItemsRequest.setExtractedText(textSnippetRequest.getContent());
        SavedItemsResponse savedItemsResponse = savedItemsService.saveItem(savedItemsRequest);
        savedItemsService.updateProcessingStatus(savedItemsResponse.getId(), ProcessingStatus.COMPLETED);
        savedItemsResponse.setProcessingStatus(ProcessingStatus.COMPLETED);
        return savedItemsResponse;
    }
}
