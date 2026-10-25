package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
<<<<<<< HEAD
import com.snapit.enums.ProcessingStatus;
import com.snapit.service.SavedItemsService;
import com.snapit.service.WebPageExtractionService;
import com.snapit.service.WebPageService;
import org.jsoup.nodes.Document;
=======
import com.snapit.service.SavedItemsService;
import com.snapit.service.WebPageExtractionService;
import com.snapit.service.WebPageService;
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
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
<<<<<<< HEAD
//        String title = webPageExtractionService.extractTitle(url);
//        String description = webPageExtractionService.extractDescription(url);
//        String text = webPageExtractionService.extractText(url);

        Document document = webPageExtractionService.fetchWebPage(url);
        String title = document.title();
        String description = document.select("meta[name=description]").attr("content");
        document.select("script, style, noscript").remove();
        String text = document.body().text();

=======
        String title = webPageExtractionService.extractTitle(url);
        String description = webPageExtractionService.extractDescription(url);
        String text = webPageExtractionService.extractText(url);
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
        SavedItemsRequest savedItemsRequest = new SavedItemsRequest();
        savedItemsRequest.setDescription(description);
        savedItemsRequest.setTitle(title);
        savedItemsRequest.setExtractedText(text);
        savedItemsRequest.setContentType(MemoryType.WEBPAGE);
        savedItemsRequest.setSourceUrl(url);
<<<<<<< HEAD
        SavedItemsResponse result = savedItemsService.saveItem(savedItemsRequest);
        result.setProcessingStatus(ProcessingStatus.COMPLETED);
        return result;
=======
        return savedItemsService.saveItem(savedItemsRequest);
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
    }
}
