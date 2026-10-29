package com.snapit.controller;

import com.snapit.dto.request.TextSnippetRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.TextSnippetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/snippets")
public class TextSnippetController {

    @Autowired
    private TextSnippetService textSnippetService;

    @PostMapping
    public ResponseEntity<SavedItemsResponse> saveTextSnippet(@Valid @RequestBody TextSnippetRequest textSnippetRequest){
        SavedItemsResponse savedItemsResponse = textSnippetService.saveTextSnippet(textSnippetRequest);
        return new ResponseEntity<>(savedItemsResponse, HttpStatus.CREATED);
    }
}
