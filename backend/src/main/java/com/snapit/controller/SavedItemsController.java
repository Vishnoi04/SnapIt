package com.snapit.controller;

import com.snapit.entity.SavedItems;
import com.snapit.service.SavedItemsService;
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
    public ResponseEntity<String> saveItems(@RequestBody SavedItems payLoad){
        SavedItems savedItem = savedItemsService.saveItem(payLoad);
        String successMessage = "Item is successfully saved with ID : " + savedItem.getId();
        return new ResponseEntity<String>(successMessage,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SavedItems>> getAllSavedItems(){
        List<SavedItems> savedItemsList = savedItemsService.getAllSavedItems();

        return new ResponseEntity<>(savedItemsList, HttpStatus.OK);
    }

    @GetMapping("/{Id}")
    public ResponseEntity<SavedItems> getSavedItemById(@PathVariable Long Id){
        Optional<SavedItems> optionalSavedItem = savedItemsService.getSavedItemById(Id);
        SavedItems savedItem = optionalSavedItem.orElseThrow();
        return new ResponseEntity<>(savedItem, HttpStatus.OK);
    }

    @DeleteMapping("/{Id}")
    public ResponseEntity<String> deleteSavedItemById(@PathVariable Long Id){
        savedItemsService.deleteSavedItemById(Id);
        String deleteMessage = "Item with ID : " + Id + " is successfully deleted.";
        return new ResponseEntity<>(deleteMessage, HttpStatus.OK);
    }

}
