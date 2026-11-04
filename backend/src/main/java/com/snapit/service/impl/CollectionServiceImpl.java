package com.snapit.service.impl;

import com.snapit.dto.request.CollectionRequest;
import com.snapit.dto.response.CollectionResponse;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.Collection;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.CollectionRepository;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.CollectionService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Locale;

@Service
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepository collectionRepository;
    private final SavedItemsRepository savedItemsRepository;
    private final ModelMapper modelMapper;

    public CollectionServiceImpl(
            CollectionRepository collectionRepository,
            SavedItemsRepository savedItemsRepository,
            ModelMapper modelMapper
    ) {
        this.collectionRepository = collectionRepository;
        this.savedItemsRepository = savedItemsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CollectionResponse create(CollectionRequest request) {
        User user = getCurrentUser();
        String name = normalizeName(request.getName());
        if (collectionRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
            throw new DuplicateResourceException("Collection name already exists.", null);
        }
        Collection collection = new Collection();
        collection.setName(name);
        collection.setDescription(request.getDescription());
        collection.setUser(user);
        return modelMapper.map(collectionRepository.save(collection), CollectionResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionResponse> getAll(Pageable pageable) {
        Long userId = getCurrentUser().getId();
        return collectionRepository.findAllByUserId(userId, pageable)
                .map(collection -> modelMapper.map(collection, CollectionResponse.class));
    }

    @Override
    @Transactional
    public CollectionResponse update(Long collectionId, CollectionRequest request) {
        Long userId = getCurrentUser().getId();
        Collection collection = findOwnedCollection(collectionId, userId);
        String name = normalizeName(request.getName());
        if (collectionRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, collectionId)) {
            throw new DuplicateResourceException("Collection name already exists.", null);
        }
        collection.setName(name);
        collection.setDescription(request.getDescription());
        return modelMapper.map(collectionRepository.save(collection), CollectionResponse.class);
    }

    @Override
    @Transactional
    public void delete(Long collectionId) {
        Long userId = getCurrentUser().getId();
        collectionRepository.delete(findOwnedCollection(collectionId, userId));
    }

    @Override
    @Transactional
    public void addItem(Long collectionId, Long savedItemId) {
        Long userId = getCurrentUser().getId();
        Collection collection = findOwnedCollection(collectionId, userId);
        SavedItems item = findOwnedItem(savedItemId, userId);
        collection.getItems().add(item);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional
    public void removeItem(Long collectionId, Long savedItemId) {
        Long userId = getCurrentUser().getId();
        Collection collection = findOwnedCollection(collectionId, userId);
        SavedItems item = findOwnedItem(savedItemId, userId);
        collection.getItems().remove(item);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedItemsResponse> getItems(Long collectionId, Pageable pageable) {
        Long userId = getCurrentUser().getId();
        findOwnedCollection(collectionId, userId);
        return collectionRepository.findItemsByCollectionIdAndUserId(collectionId, userId, pageable)
                .map(item -> modelMapper.map(item, SavedItemsResponse.class));
    }

    private Collection findOwnedCollection(Long id, Long userId) {
        return collectionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with id: " + id));
    }

    private SavedItems findOwnedItem(Long id, Long userId) {
        return savedItemsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved item not found with id: " + id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
        return user;
    }

    private String normalizeName(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
