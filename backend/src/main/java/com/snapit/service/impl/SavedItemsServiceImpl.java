package com.snapit.service.impl;

import com.snapit.entity.SavedItems;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.SavedItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SavedItemsServiceImpl implements SavedItemsService {
    @Autowired
    private SavedItemsRepository savedItemsRepository;

    @Override
    public SavedItems saveItem(SavedItems saveItem) {
        return savedItemsRepository.save(saveItem);
    }

    @Override
    public List<SavedItems> getAllSavedItems() {
        return savedItemsRepository.findAll();
    }

    @Override
    public Optional<SavedItems> getSavedItemById(Long Id) {
        return savedItemsRepository.findById(Id);
    }

    @Override
    public void deleteSavedItemById(Long Id) {
       savedItemsRepository.deleteById(Id);
    }
}
