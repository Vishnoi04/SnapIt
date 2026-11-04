package com.snapit.service.impl;

import com.snapit.dto.request.ShareSavedItemRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.ShareResponse;
import com.snapit.entity.SavedItemShare;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemShareRepository;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.repository.UserRepository;
import com.snapit.service.ShareService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShareServiceImpl implements ShareService {
    private final SavedItemShareRepository shareRepository;
    private final SavedItemsRepository savedItemsRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ShareServiceImpl(
            SavedItemShareRepository shareRepository,
            SavedItemsRepository savedItemsRepository,
            UserRepository userRepository,
            ModelMapper modelMapper
    ) {
        this.shareRepository = shareRepository;
        this.savedItemsRepository = savedItemsRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public ShareResponse share(ShareSavedItemRequest request) {
        User owner = getCurrentUser();
        SavedItems item = savedItemsRepository.findByIdAndUserId(request.getSavedItemId(), owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + request.getSavedItemId()
                ));
        User recipient = userRepository.findByUsername(request.getRecipientUsername().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient profile not found."));
        if (owner.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("You cannot share an item with yourself.");
        }
        if (shareRepository.existsBySavedItemIdAndRecipientId(item.getId(), recipient.getId())) {
            throw new DuplicateResourceException("This item is already shared with that user.", null);
        }

        SavedItemShare share = new SavedItemShare();
        share.setSavedItem(item);
        share.setRecipient(recipient);
        return toShareResponse(shareRepository.save(share));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShareResponse> getSentShares(Pageable pageable) {
        return shareRepository.findAllBySavedItemUserId(getCurrentUser().getId(), pageable)
                .map(this::toShareResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavedItemsResponse> getReceivedShares(Pageable pageable) {
        return shareRepository.findAllByRecipientId(getCurrentUser().getId(), pageable)
                .map(share -> modelMapper.map(share.getSavedItem(), SavedItemsResponse.class));
    }

    @Override
    @Transactional(readOnly = true)
    public SavedItemsResponse getReceivedSharedItem(Long savedItemId) {
        SavedItemShare share = shareRepository
                .findBySavedItemIdAndRecipientId(savedItemId, getCurrentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shared item not found with id: " + savedItemId
                ));
        return modelMapper.map(share.getSavedItem(), SavedItemsResponse.class);
    }

    @Override
    @Transactional
    public void revoke(Long shareId) {
        User owner = getCurrentUser();
        SavedItemShare share = shareRepository.findByIdAndSavedItemUserId(shareId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Share not found with id: " + shareId));
        shareRepository.delete(share);
    }

    private ShareResponse toShareResponse(SavedItemShare share) {
        ShareResponse response = new ShareResponse();
        response.setId(share.getId());
        response.setSavedItemId(share.getSavedItem().getId());
        response.setSavedItemTitle(share.getSavedItem().getTitle());
        response.setRecipientUsername(share.getRecipient().getUsername());
        response.setSharedAt(share.getSharedAt());
        return response;
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
}
