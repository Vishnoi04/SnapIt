package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.SavedItemsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.isAuthenticated()) || !(authentication.getPrincipal() instanceof User)){
            throw  new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
        User user = (User)authentication.getPrincipal();
        SavedItems savedItemsForRequest = modelMapper.map(saveItem, SavedItems.class);
        savedItemsForRequest.setUser(user);
        SavedItems saveItemsResult = savedItemsRepository.save(savedItemsForRequest);
        return modelMapper.map(saveItemsResult, SavedItemsResponse.class);
    }

    @Override
    public List<SavedItemsResponse> getAllSavedItems() {
        Long userId = getAuthenticatedUserId();

        return savedItemsRepository.findAllByUserId(userId)
                .stream()
                .map(item -> modelMapper.map(item, SavedItemsResponse.class))
                .toList();
    }

    @Override
    public SavedItemsResponse getSavedItemById(Long id) {
        Long userId = getAuthenticatedUserId();

        SavedItems savedItem = savedItemsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with ID: " + id
                ));
        return modelMapper.map(savedItem, SavedItemsResponse.class);
    }
    @Override
    public void deleteSavedItemById(Long Id) {
        long userId = getAuthenticatedUserId();
        SavedItems existingItem = savedItemsRepository
                .findByIdAndUserId(Id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + Id
                ));

        savedItemsRepository.delete(existingItem);
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
        Long userId = getAuthenticatedUserId();
        return savedItemsRepository
                .searchItems(userId, keyword, contentType, pageable)
                .map(savedItem ->
                        modelMapper.map(savedItem, SavedItemsResponse.class));
    }


    private Long getAuthenticatedUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated user not found"
            );
        }

        return user.getId();
    }

    @Override
    public SavedItemsResponse updateSavedItem(
            Long id,
            SavedItemsRequest request
    ) {
        Long userId = getAuthenticatedUserId();
        SavedItems existingItem = savedItemsRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + id
                ));

        existingItem.setTitle(request.getTitle());
        existingItem.setSourceUrl(request.getSourceUrl());
        existingItem.setContentType(request.getContentType());
        existingItem.setDescription(request.getDescription());
        existingItem.setExtractedText(request.getExtractedText());
        existingItem.setFilePath(request.getFilePath());
        existingItem.setLanguage(request.getLanguage());
        SavedItems updatedItem = savedItemsRepository.save(existingItem);
        return modelMapper.map(updatedItem, SavedItemsResponse.class);
    }


}
