package com.snapit.service.impl;

import com.snapit.dto.request.ReminderRequest;
import com.snapit.entity.Reminder;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.ReminderRepository;
import com.snapit.repository.SavedItemsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderServiceOwnershipTest {
    private static final long CURRENT_USER_ID = 101L;
    private static final long OTHER_USERS_ITEM_ID = 202L;
    private static final long OTHER_USERS_REMINDER_ID = 303L;

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private SavedItemsRepository savedItemsRepository;

    @InjectMocks
    private ReminderServiceImpl reminderService;

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
    void cannotCreateReminderForAnotherUsersItem() {
        ReminderRequest request = requestFor(OTHER_USERS_ITEM_ID);
        when(savedItemsRepository.findByIdAndUserId(OTHER_USERS_ITEM_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reminderService.create(request));

        verify(reminderRepository, never()).save(org.mockito.ArgumentMatchers.any(Reminder.class));
    }

    @Test
    void cannotReadAnotherUsersReminder() {
        when(reminderRepository.findByIdAndSavedItemUserId(OTHER_USERS_REMINDER_ID, CURRENT_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reminderService.updateCompletion(OTHER_USERS_REMINDER_ID, true)
        );

        verify(reminderRepository, never()).save(org.mockito.ArgumentMatchers.any(Reminder.class));
    }

    @Test
    void createsReminderOnlyAfterFindingOwnedItem() {
        SavedItems ownedItem = new SavedItems();
        ownedItem.setId(1L);
        ownedItem.setTitle("Owned note");
        ReminderRequest request = requestFor(1L);
        when(savedItemsRepository.findByIdAndUserId(1L, CURRENT_USER_ID))
                .thenReturn(Optional.of(ownedItem));
        when(reminderRepository.save(org.mockito.ArgumentMatchers.any(Reminder.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        reminderService.create(request);

        verify(savedItemsRepository).findByIdAndUserId(1L, CURRENT_USER_ID);
        verify(reminderRepository).save(org.mockito.ArgumentMatchers.argThat(reminder ->
                reminder.getSavedItem() == ownedItem
                        && reminder.getRemindAt().equals(request.getRemindAt())
        ));
    }

    @Test
    void dueRemindersQueryIsScopedToAuthenticatedUser() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(reminderRepository
                .findAllBySavedItemUserIdAndCompletedFalseAndRemindAtLessThanEqual(
                        org.mockito.ArgumentMatchers.eq(CURRENT_USER_ID),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.eq(pageable)
                ))
                .thenReturn(Page.empty(pageable));

        reminderService.getDue(pageable);

        verify(reminderRepository)
                .findAllBySavedItemUserIdAndCompletedFalseAndRemindAtLessThanEqual(
                        org.mockito.ArgumentMatchers.eq(CURRENT_USER_ID),
                        org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                        org.mockito.ArgumentMatchers.eq(pageable)
                );
    }

    private ReminderRequest requestFor(Long savedItemId) {
        ReminderRequest request = new ReminderRequest();
        request.setSavedItemId(savedItemId);
        request.setRemindAt(LocalDateTime.now().plusDays(1));
        request.setMessage("Review this item");
        return request;
    }
}
