package com.snapit.controller;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.service.SavedItemsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/saved-items")
public class SavedItemsController {
    @Autowired
    private SavedItemsService savedItemsService;

    @PostMapping
    public ResponseEntity<String> saveItems(@Valid @RequestBody SavedItemsRequest payLoad) {
        SavedItemsResponse savedItem = savedItemsService.saveItem(payLoad);
        String successMessage =
                "Item is successfully saved with ID : " + savedItem.getId();
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SavedItemsResponse>> getAllSavedItems() {
        List<SavedItemsResponse> savedItemsList =
                savedItemsService.getAllSavedItems();
        return new ResponseEntity<>(savedItemsList, HttpStatus.OK);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<SavedItemsResponse> getSavedItemById(
            @PathVariable Long Id) {
        SavedItemsResponse savedItem = savedItemsService.getSavedItemById(Id);
        if (savedItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(savedItem);
    }
 
    @DeleteMapping("/{Id}")
    public ResponseEntity<String> deleteSavedItemById(@PathVariable Long Id){
        savedItemsService.deleteSavedItemById(Id);
        String deleteMessage = "Item with ID : " + Id + " is successfully deleted.";
        return new ResponseEntity<>(deleteMessage, HttpStatus.OK);
    }

}
