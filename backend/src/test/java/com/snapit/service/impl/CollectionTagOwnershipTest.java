package com.snapit.service.impl;

import com.snapit.entity.Collection;
import com.snapit.entity.Tag;
import com.snapit.entity.User;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.CollectionRepository;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectionTagOwnershipTest {
    private static final long CURRENT_USER_ID = 101L;
    private static final long OTHER_USERS_ITEM_ID = 202L;

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SavedItemsRepository savedItemsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    @InjectMocks
    private TagServiceImpl tagService;

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
    void cannotAddAnotherUsersItemToOwnedCollection() {
        Collection collection = new Collection();
        when(collectionRepository.findByIdAndUserId(1L, CURRENT_USER_ID))
                .thenReturn(Optional.of(collection));
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> collectionService.addItem(1L, OTHER_USERS_ITEM_ID));

        verify(collectionRepository, never()).save(collection);
    }

    @Test
    void cannotAddAnotherUsersItemToOwnedTag() {
        Tag tag = new Tag();
        when(tagRepository.findByIdAndUserId(1L, CURRENT_USER_ID)).thenReturn(Optional.of(tag));
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tagService.addItem(1L, OTHER_USERS_ITEM_ID));

        verify(tagRepository, never()).save(tag);
    }

    @Test
    void cannotReadAnotherUsersCollectionItems() {
        when(collectionRepository.findByIdAndUserId(1L, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> collectionService.getItems(1L, org.springframework.data.domain.PageRequest.of(0, 10)));

        verify(collectionRepository, never()).findItemsByCollectionIdAndUserId(
                1L,
                CURRENT_USER_ID,
                org.springframework.data.domain.PageRequest.of(0, 10)
        );
    }
}
