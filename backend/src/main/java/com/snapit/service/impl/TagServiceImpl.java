package com.snapit.service.impl;

import com.snapit.dto.request.TagRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.TagResponse;
import com.snapit.entity.SavedItems;
import com.snapit.entity.Tag;
import com.snapit.entity.User;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.repository.TagRepository;
import com.snapit.service.TagService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final SavedItemsRepository savedItemsRepository;
    private final ModelMapper modelMapper;

    public TagServiceImpl(
            TagRepository tagRepository,
            SavedItemsRepository savedItemsRepository,
            ModelMapper modelMapper
    ) {
        this.tagRepository = tagRepository;
        this.savedItemsRepository = savedItemsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public TagResponse create(TagRequest request) {
        User user = getCurrentUser();
        String name = normalizeName(request.getName());
        if (tagRepository.existsByUserIdAndNameIgnoreCase(user.getId(), name)) {
            throw new DuplicateResourceException("Tag name already exists.", null);
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag.setUser(user);
        return modelMapper.map(tagRepository.save(tag), TagResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TagResponse> getAll(Pageable pageable) {
        Long userId = getCurrentUser().getId();
        return tagRepository.findAllByUserId(userId, pageable)
                .map(tag -> modelMapper.map(tag, TagResponse.class));
    }

    @Override
    @Transactional
    public TagResponse update(Long tagId, TagRequest request) {
        Long userId = getCurrentUser().getId();
        Tag tag = findOwnedTag(tagId, userId);
        String name = normalizeName(request.getName());
        if (tagRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, tagId)) {
            throw new DuplicateResourceException("Tag name already exists.", null);
        }
        tag.setName(name);
        return modelMapper.map(tagRepository.save(tag), TagResponse.class);
    }

    @Override
    @Transactional
    public void delete(Long tagId) {
        Long userId = getCurrentUser().getId();
        tagRepository.delete(findOwnedTag(tagId, userId));
    }

    @Override
    @Transactional
    public void addItem(Long tagId, Long savedItemId) {
        Long userId = getCurrentUser().getId();
        Tag tag = findOwnedTag(tagId, userId);
        SavedItems item = findOwnedItem(savedItemId, userId);
        tag.getItems().add(item);
        tagRepository.save(tag);
    }

    @Override
    @Transactional
    public void removeItem(Long tagId, Long savedItemId) {
        Long userId = getCurrentUser().getId();
        Tag tag = findOwnedTag(tagId, userId);
        SavedItems item = findOwnedItem(savedItemId, userId);
        tag.getItems().remove(item);
        tagRepository.save(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedItemsResponse> getItems(Long tagId, Pageable pageable) {
        Long userId = getCurrentUser().getId();
        findOwnedTag(tagId, userId);
        return tagRepository.findItemsByTagIdAndUserId(tagId, userId, pageable)
                .map(item -> modelMapper.map(item, SavedItemsResponse.class));
    }

    private Tag findOwnedTag(Long id, Long userId) {
        return tagRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
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
