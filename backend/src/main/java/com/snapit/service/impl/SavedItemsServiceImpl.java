package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.SavedItems;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.SavedItemsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedItemsServiceImpl implements SavedItemsService {
    @Autowired
    private SavedItemsRepository savedItemsRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SavedItemsResponse saveItem(SavedItemsRequest saveItem) {
        SavedItems savedItemsForRequest = modelMapper.map(saveItem, SavedItems.class);
        SavedItems saveItemsResult = savedItemsRepository.save(savedItemsForRequest);
        return modelMapper.map(saveItemsResult, SavedItemsResponse.class);
    }

    @Override
    public List<SavedItemsResponse> getAllSavedItems() {
        return savedItemsRepository.findAll()
                .stream()
                .map(item -> modelMapper.map(item, SavedItemsResponse.class))
                .toList();
    }

    @Override
    public SavedItemsResponse getSavedItemById(Long id) {
        SavedItems savedItem = savedItemsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with ID: " + id
                ));
        return modelMapper.map(savedItem, SavedItemsResponse.class);
    }
    @Override
    public void deleteSavedItemById(Long Id) {
        if (!savedItemsRepository.existsById(Id)) {
            throw new ResourceNotFoundException(
                    "Saved item not found with ID: " + Id
            );
        }
        savedItemsRepository.deleteById(Id);
    }

    @Override
    public void updateProcessingStatus(Long Id, ProcessingStatus processingStatus) {
        SavedItems savedItems = savedItemsRepository.findById(Id).orElseThrow(()-> new ResourceNotFoundException("Saved items not found with Id : " + Id));
        savedItemsRepository.save(savedItems);
    }

    @Override
    public Page<SavedItemsResponse> searchItems(
            String keyword,
            MemoryType contentType,
            Pageable pageable) {

        return savedItemsRepository
                .searchItems(keyword, contentType, pageable)
                .map(savedItem ->
                        modelMapper.map(savedItem, SavedItemsResponse.class));
    }

}
