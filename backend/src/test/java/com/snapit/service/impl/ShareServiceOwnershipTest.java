package com.snapit.service.impl;

import com.snapit.dto.request.ShareSavedItemRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.SavedItemShare;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.SavedItemShareRepository;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareServiceOwnershipTest {
    private static final long CURRENT_USER_ID = 101L;
    private static final long SAVED_ITEM_ID = 202L;
    private static final long SHARE_ID = 303L;

    @Mock
    private SavedItemShareRepository shareRepository;

    @Mock
    private SavedItemsRepository savedItemsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShareServiceImpl shareService;

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
    void cannotShareAnotherUsersItem() {
        ShareSavedItemRequest request = new ShareSavedItemRequest();
        request.setSavedItemId(SAVED_ITEM_ID);
        request.setRecipientUsername("recipient");
        when(savedItemsRepository.findByIdAndUserId(SAVED_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shareService.share(request));

        verify(userRepository, never()).findByUsername("recipient");
        verify(shareRepository, never()).save(any(SavedItemShare.class));
    }

    @Test
    void cannotRetrieveAnItemNotSharedWithCurrentUser() {
        when(shareRepository.findBySavedItemIdAndRecipientId(SAVED_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> shareService.getReceivedSharedItem(SAVED_ITEM_ID)
        );

        verify(modelMapper, never()).map(any(), org.mockito.ArgumentMatchers.eq(SavedItemsResponse.class));
    }

    @Test
    void cannotRevokeAnotherUsersShare() {
        when(shareRepository.findByIdAndSavedItemUserId(SHARE_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> shareService.revoke(SHARE_ID));

        verify(shareRepository, never()).delete(any(SavedItemShare.class));
    }

    @Test
    void createsShareForOwnedItemAndDifferentRecipient() {
        User owner = new User();
        owner.setId(CURRENT_USER_ID);
        User recipient = new User();
        recipient.setId(404L);
        recipient.setUsername("recipient");
        SavedItems item = new SavedItems();
        item.setId(SAVED_ITEM_ID);
        item.setUser(owner);
        ShareSavedItemRequest request = new ShareSavedItemRequest();
        request.setSavedItemId(SAVED_ITEM_ID);
        request.setRecipientUsername(" recipient ");
        when(savedItemsRepository.findByIdAndUserId(SAVED_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.of(item));
        when(userRepository.findByUsername("recipient")).thenReturn(Optional.of(recipient));
        when(shareRepository.existsBySavedItemIdAndRecipientId(SAVED_ITEM_ID, recipient.getId()))
                .thenReturn(false);
        when(shareRepository.save(any(SavedItemShare.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        shareService.share(request);

        verify(shareRepository).save(org.mockito.ArgumentMatchers.argThat(share ->
                share.getSavedItem() == item && share.getRecipient() == recipient
        ));
    }
}
