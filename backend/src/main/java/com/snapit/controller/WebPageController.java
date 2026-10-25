package com.snapit.controller;

import com.snapit.dto.request.WebPageRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.WebPageService;
import jakarta.validation.Valid;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/webpages")
public class WebPageController {
    @Autowired
    private WebPageService webPageService;

    @PostMapping
    public ResponseEntity<?> saveWebPage(@Valid @RequestBody WebPageRequest payLoad){
        String url = payLoad.getUrl();
        SavedItemsResponse savedItemsResponse = webPageService.saveWebPage(url);
        return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
    }

}
