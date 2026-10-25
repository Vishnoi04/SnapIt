package com.snapit.controller;

<<<<<<< HEAD
import com.snapit.dto.request.WebPageRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.WebPageService;
import jakarta.validation.Valid;
=======
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.WebPageService;
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
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
<<<<<<< HEAD
    public ResponseEntity<?> saveWebPage(@Valid @RequestBody WebPageRequest payLoad){
        String url = payLoad.getUrl();
=======
    public ResponseEntity<?> saveWebPage(@RequestBody Map<String, String> payLoad){
        String url = payLoad.get("url");
        if (url == null || url.isBlank()){
            return new ResponseEntity<>(Map.of("Message : ", "URL is required"), HttpStatus.BAD_REQUEST);
        }
>>>>>>> 7ae5282221ce9905fabc40be185806a7df85ebae
        SavedItemsResponse savedItemsResponse = webPageService.saveWebPage(url);
        return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
    }

}
