package com.snapit.service.impl;

import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavedItemsServiceOwnershipTest {
    @Mock
    private SavedItemsRepository savedItemsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private SavedItemsServiceImpl savedItemsService;

    private static final long CURRENT_USER_ID = 101L;
    private static final long OTHER_USERS_ITEM_ID = 202L;

    @BeforeEach
    void authenticateCurrentUser() {
        User currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(currentUser, null, java.util.List.of())
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void readsItemsOnlyForAuthenticatedUser() {
        when(savedItemsRepository.findAllByUserId(CURRENT_USER_ID)).thenReturn(java.util.List.of());

        savedItemsService.getAllSavedItems();

        verify(savedItemsRepository).findAllByUserId(CURRENT_USER_ID);
        verify(savedItemsRepository, never()).findAll();
    }

    @Test
    void retrievesPagedItemsOnlyForAuthenticatedUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(savedItemsRepository.findAllByUserId(CURRENT_USER_ID, pageable))
                .thenReturn(Page.empty(pageable));

        savedItemsService.getAllSavedItems(pageable);

        verify(savedItemsRepository).findAllByUserId(CURRENT_USER_ID, pageable);
        verify(savedItemsRepository, never()).findAll();
    }

    @Test
    void hidesAnotherUsersItemWhenReadingById() {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedItemsService.getSavedItemById(OTHER_USERS_ITEM_ID));

        verify(savedItemsRepository).findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID);
        verify(savedItemsRepository, never()).findById(OTHER_USERS_ITEM_ID);
    }

    @Test
    void doesNotUpdateAnotherUsersItem() {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedItemsService.updateProcessingStatus(
                        OTHER_USERS_ITEM_ID,
                        ProcessingStatus.COMPLETED
                ));

        verify(savedItemsRepository, never()).save(org.mockito.ArgumentMatchers.any(SavedItems.class));
    }

    @Test
    void doesNotEditAnotherUsersItem() {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedItemsService.updateSavedItem(OTHER_USERS_ITEM_ID, new SavedItemsRequest()));

        verify(savedItemsRepository, never()).save(org.mockito.ArgumentMatchers.any(SavedItems.class));
    }

    @Test
    void doesNotDeleteAnotherUsersItem() {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedItemsService.deleteSavedItemById(OTHER_USERS_ITEM_ID));

        verify(savedItemsRepository, never()).delete(org.mockito.ArgumentMatchers.any(SavedItems.class));
    }

    @Test
    void searchesOnlyAuthenticatedUsersItems() {
        when(savedItemsRepository.searchItems(
                org.mockito.ArgumentMatchers.eq(CURRENT_USER_ID),
                org.mockito.ArgumentMatchers.eq("private"),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.any(PageRequest.class)
        )).thenReturn(Page.empty());

        savedItemsService.searchItems("private", null, null, null, PageRequest.of(0, 10));

        verify(savedItemsRepository).searchItems(
                CURRENT_USER_ID,
                "private",
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );
    }

    @Test
    void retrievesOnlyCurrentUsersFavorites() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(savedItemsRepository.findAllByUserIdAndFavoriteTrue(CURRENT_USER_ID, pageable))
                .thenReturn(Page.empty(pageable));

        savedItemsService.getFavoriteSavedItems(pageable);

        verify(savedItemsRepository).findAllByUserIdAndFavoriteTrue(CURRENT_USER_ID, pageable);
    }

    @Test
    void updatesFavoriteForOwnedItem() {
        SavedItems ownedItem = new SavedItems();
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.of(ownedItem));
        when(savedItemsRepository.save(ownedItem)).thenReturn(ownedItem);
        when(modelMapper.map(ownedItem, SavedItemsResponse.class))
                .thenReturn(new SavedItemsResponse());

        savedItemsService.updateFavorite(OTHER_USERS_ITEM_ID, true);

        org.junit.jupiter.api.Assertions.assertTrue(ownedItem.isFavorite());
        verify(savedItemsRepository).save(ownedItem);
    }

    @Test
    void cannotUpdateFavoriteOnAnotherUsersItem() {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedItemsService.updateFavorite(OTHER_USERS_ITEM_ID, true));

        verify(savedItemsRepository, never()).save(org.mockito.ArgumentMatchers.any(SavedItems.class));
    }

    @Test
    void updatesProcessingStatusForOwnedItem() {
        SavedItems ownedItem = new SavedItems();
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.of(ownedItem));
        when(savedItemsRepository.save(ownedItem)).thenReturn(ownedItem);

        savedItemsService.updateProcessingStatus(OTHER_USERS_ITEM_ID, ProcessingStatus.COMPLETED);

        org.junit.jupiter.api.Assertions.assertEquals(
                ProcessingStatus.COMPLETED,
                ownedItem.getProcessingStatus()
        );
        verify(savedItemsRepository).save(ownedItem);
    }

    @Test
    void rejectsDuplicateWebpageAfterUrlNormalizationForSameUser() {
        SavedItemsRequest request = new SavedItemsRequest();
        request.setContentType(MemoryType.WEBPAGE);
        request.setSourceUrl("HTTP://Example.COM:80/path/#section");
        SavedItems existingItem = new SavedItems();
        existingItem.setSourceUrl("http://example.com/path");
        SavedItems mappedItem = new SavedItems();
        mappedItem.setContentType(MemoryType.WEBPAGE);
        mappedItem.setSourceUrl(request.getSourceUrl());
        when(modelMapper.map(request, SavedItems.class)).thenReturn(mappedItem);
        when(savedItemsRepository.findSourceUrlsByUserIdAndContentType(
                CURRENT_USER_ID,
                MemoryType.WEBPAGE
        )).thenReturn(java.util.List.of(existingItem.getSourceUrl()));

        assertThrows(DuplicateResourceException.class, () -> savedItemsService.saveItem(request));

        verify(savedItemsRepository, never()).save(any(SavedItems.class));
    }

    @Test
    void duplicateWebpageCheckIsScopedToCurrentUserAndStoresNormalizedUrl() {
        SavedItemsRequest request = new SavedItemsRequest();
        request.setContentType(MemoryType.WEBPAGE);
        request.setSourceUrl("HTTPS://Example.COM:443/docs///#part");
        SavedItems mappedItem = new SavedItems();
        mappedItem.setContentType(MemoryType.WEBPAGE);
        mappedItem.setSourceUrl(request.getSourceUrl());
        when(modelMapper.map(request, SavedItems.class)).thenReturn(mappedItem);
        when(savedItemsRepository.findSourceUrlsByUserIdAndContentType(
                CURRENT_USER_ID,
                MemoryType.WEBPAGE
        )).thenReturn(java.util.List.of());
        when(savedItemsRepository.save(mappedItem)).thenReturn(mappedItem);
        when(modelMapper.map(mappedItem, SavedItemsResponse.class)).thenReturn(new SavedItemsResponse());

        savedItemsService.saveItem(request);

        verify(savedItemsRepository).findSourceUrlsByUserIdAndContentType(
                CURRENT_USER_ID,
                MemoryType.WEBPAGE
        );
        verify(savedItemsRepository).save(mappedItem);
        org.junit.jupiter.api.Assertions.assertEquals(
                "https://example.com/docs",
                mappedItem.getSourceUrl()
        );
        org.junit.jupiter.api.Assertions.assertNotNull(mappedItem.getUser());
    }

    @Test
    void cannotDownloadAnotherUsersFile() throws IOException {
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> savedItemsService.getOwnedFile(OTHER_USERS_ITEM_ID)
        );

        verify(fileStorageService, never()).loadFile(any());
    }

    @Test
    void downloadsOwnedFileWithoutExposingStoragePath() throws IOException {
        SavedItems ownedItem = new SavedItems();
        ownedItem.setFilePath("C:\\snapit\\uploads\\safe-id.pdf");
        Resource resource = org.mockito.Mockito.mock(Resource.class);
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.of(ownedItem));
        when(fileStorageService.loadFile(ownedItem.getFilePath())).thenReturn(resource);

        org.junit.jupiter.api.Assertions.assertSame(
                resource,
                savedItemsService.getOwnedFile(OTHER_USERS_ITEM_ID)
        );

        verify(fileStorageService).loadFile(ownedItem.getFilePath());
    }

    @Test
    void deletesOwnedFileAndClearsItsDatabaseReference() throws IOException {
        SavedItems ownedItem = new SavedItems();
        ownedItem.setFilePath("C:\\snapit\\uploads\\safe-id.pdf");
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.of(ownedItem));
        when(savedItemsRepository.save(ownedItem)).thenReturn(ownedItem);

        savedItemsService.deleteOwnedFile(OTHER_USERS_ITEM_ID);

        verify(fileStorageService).deleteFile("C:\\snapit\\uploads\\safe-id.pdf");
        org.junit.jupiter.api.Assertions.assertNull(ownedItem.getFilePath());
        verify(savedItemsRepository).save(ownedItem);
    }
}
