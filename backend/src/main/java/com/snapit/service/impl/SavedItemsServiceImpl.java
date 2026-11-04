package com.snapit.service.impl;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.exception.FileStorageException;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.SavedItemsService;
import com.snapit.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

@Service
public class SavedItemsServiceImpl implements SavedItemsService {
    @Autowired
    private SavedItemsRepository savedItemsRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public SavedItemsResponse saveItem(SavedItemsRequest saveItem) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.isAuthenticated()) || !(authentication.getPrincipal() instanceof User)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
        User user = (User) authentication.getPrincipal();
        SavedItems savedItemsForRequest = modelMapper.map(saveItem, SavedItems.class);
        if (savedItemsForRequest.getContentType() == MemoryType.WEBPAGE
                && savedItemsForRequest.getSourceUrl() != null
                && !savedItemsForRequest.getSourceUrl().isBlank()) {
            String normalizedUrl = normalizeWebPageUrl(savedItemsForRequest.getSourceUrl());
            ensureWebpageUrlIsUnique(user.getId(), normalizedUrl);
            savedItemsForRequest.setSourceUrl(normalizedUrl);
        }
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
    public Page<SavedItemsResponse> getAllSavedItems(Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        return savedItemsRepository.findAllByUserId(userId, pageable)
                .map(item -> modelMapper.map(item, SavedItemsResponse.class));
    }

    @Override
    public Page<SavedItemsResponse> getFavoriteSavedItems(Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        return savedItemsRepository.findAllByUserIdAndFavoriteTrue(userId, pageable)
                .map(item -> modelMapper.map(item, SavedItemsResponse.class));
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

        deleteStoredFile(existingItem);
        savedItemsRepository.delete(existingItem);
    }

    @Override
    public Resource getOwnedFile(Long id) {
        SavedItems savedItem = findOwnedItem(id);
        if (savedItem.getFilePath() == null || savedItem.getFilePath().isBlank()) {
            throw new ResourceNotFoundException("File not found for saved item: " + id);
        }
        try {
            return fileStorageService.loadFile(savedItem.getFilePath());
        } catch (NoSuchFileException exception) {
            throw new ResourceNotFoundException("File not found for saved item: " + id);
        } catch (IOException exception) {
            throw new FileStorageException("Unable to read saved item file.", exception);
        } catch (IllegalArgumentException exception) {
            throw new FileStorageException("Unable to read saved item file.", exception);
        }
    }

    @Override
    public void deleteOwnedFile(Long id) {
        SavedItems savedItem = findOwnedItem(id);
        if (savedItem.getFilePath() == null || savedItem.getFilePath().isBlank()) {
            throw new ResourceNotFoundException("File not found for saved item: " + id);
        }
        try {
            fileStorageService.deleteFile(savedItem.getFilePath());
        } catch (IOException exception) {
            throw new FileStorageException("Unable to delete saved item file.", exception);
        } catch (IllegalArgumentException exception) {
            throw new FileStorageException("Unable to delete saved item file.", exception);
        }
        savedItem.setFilePath(null);
        savedItemsRepository.save(savedItem);
    }

    private SavedItems findOwnedItem(Long id) {
        return savedItemsRepository.findByIdAndUserId(id, getAuthenticatedUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + id
                ));
    }

    private void deleteStoredFile(SavedItems savedItem) {
        if (savedItem.getFilePath() == null || savedItem.getFilePath().isBlank()) {
            return;
        }
        try {
            fileStorageService.deleteFile(savedItem.getFilePath());
        } catch (IOException exception) {
            throw new FileStorageException("Unable to delete saved item file.", exception);
        } catch (IllegalArgumentException exception) {
            throw new FileStorageException("Unable to delete saved item file.", exception);
        }
    }

    @Override
    public void updateProcessingStatus(Long Id, ProcessingStatus processingStatus) {
        Long userId = getAuthenticatedUserId();
        SavedItems savedItems = savedItemsRepository.findByIdAndUserId(Id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + Id
                ));
        savedItems.setProcessingStatus(processingStatus);
        savedItemsRepository.save(savedItems);
    }

    @Override
    public Page<SavedItemsResponse> searchItems(
            String keyword,
            MemoryType contentType,
            Boolean favorite,
            Long tagId,
            Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        return savedItemsRepository
                .searchItems(
                        userId,
                        keyword == null ? null : keyword.trim(),
                        contentType,
                        favorite,
                        tagId,
                        pageable
                )
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

        if (request.getContentType() == MemoryType.WEBPAGE
                && request.getSourceUrl() != null
                && !request.getSourceUrl().isBlank()) {
            String normalizedUrl = normalizeWebPageUrl(request.getSourceUrl());
            if (!normalizedUrl.equals(tryNormalizeWebPageUrl(existingItem.getSourceUrl()))) {
                ensureWebpageUrlIsUnique(userId, normalizedUrl);
            }
            existingItem.setSourceUrl(normalizedUrl);
        } else {
            existingItem.setSourceUrl(request.getSourceUrl());
        }
        existingItem.setTitle(request.getTitle());
        existingItem.setContentType(request.getContentType());
        existingItem.setDescription(request.getDescription());
        existingItem.setExtractedText(request.getExtractedText());
        existingItem.setLanguage(request.getLanguage());
        SavedItems updatedItem = savedItemsRepository.save(existingItem);
        return modelMapper.map(updatedItem, SavedItemsResponse.class);
    }

    @Override
    public SavedItemsResponse updateFavorite(Long id, boolean favorite) {
        Long userId = getAuthenticatedUserId();
        SavedItems savedItem = savedItemsRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + id
                ));
        savedItem.setFavorite(favorite);
        return modelMapper.map(savedItemsRepository.save(savedItem), SavedItemsResponse.class);
    }

    private String normalizeWebPageUrl(String sourceUrl) {
        try {
            URI uri = new URI(sourceUrl.trim()).normalize();
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || host == null
                    || uri.getUserInfo() != null
                    || uri.getPort() > 65535) {
                throw new IllegalArgumentException("Webpage URL must be a valid HTTP or HTTPS address.");
            }

            scheme = scheme.toLowerCase(Locale.ROOT);
            host = host.toLowerCase(Locale.ROOT);
            int port = uri.getPort();
            if ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443)) {
                port = -1;
            }

            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            } else {
                while (path.length() > 1 && path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
            }

            StringBuilder normalized = new StringBuilder(scheme)
                    .append("://");
            if (host.contains(":")) {
                normalized.append(host.startsWith("[") ? host : "[" + host + "]");
            } else {
                normalized.append(host);
            }
            if (port != -1) {
                normalized.append(':').append(port);
            }
            normalized.append(path);
            if (uri.getRawQuery() != null) {
                normalized.append('?').append(uri.getRawQuery());
            }
            return normalized.toString();
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Webpage URL must be a valid HTTP or HTTPS address.", exception);
        }
    }

    private void ensureWebpageUrlIsUnique(Long userId, String normalizedUrl) {
        boolean duplicate = savedItemsRepository
                .findSourceUrlsByUserIdAndContentType(userId, MemoryType.WEBPAGE)
                .stream()
                .map(this::tryNormalizeWebPageUrl)
                .anyMatch(existingUrl -> normalizedUrl.equals(existingUrl));
        if (duplicate) {
            throw new DuplicateResourceException("This webpage has already been saved.", null);
        }
    }

    private String tryNormalizeWebPageUrl(String sourceUrl) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return null;
        }
        try {
            return normalizeWebPageUrl(sourceUrl);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}
