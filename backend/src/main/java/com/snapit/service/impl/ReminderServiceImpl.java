package com.snapit.service.impl;

import com.snapit.dto.request.ReminderRequest;
import com.snapit.dto.response.ReminderResponse;
import com.snapit.entity.Reminder;
import com.snapit.entity.SavedItems;
import com.snapit.entity.User;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.ReminderRepository;
import com.snapit.repository.SavedItemsRepository;
import com.snapit.service.ReminderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReminderServiceImpl implements ReminderService {
    private final ReminderRepository reminderRepository;
    private final SavedItemsRepository savedItemsRepository;

    public ReminderServiceImpl(
            ReminderRepository reminderRepository,
            SavedItemsRepository savedItemsRepository
    ) {
        this.reminderRepository = reminderRepository;
        this.savedItemsRepository = savedItemsRepository;
    }

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        Long userId = getCurrentUserId();
        SavedItems savedItem = findOwnedItem(request.getSavedItemId(), userId);
        Reminder reminder = new Reminder();
        reminder.setSavedItem(savedItem);
        reminder.setMessage(request.getMessage());
        reminder.setRemindAt(request.getRemindAt());
        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderResponse> getAll(Boolean completed, Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<Reminder> reminders = completed == null
                ? reminderRepository.findAllBySavedItemUserId(userId, pageable)
                : reminderRepository.findAllBySavedItemUserIdAndCompleted(userId, completed, pageable);
        return reminders.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReminderResponse> getDue(Pageable pageable) {
        return reminderRepository
                .findAllBySavedItemUserIdAndCompletedFalseAndRemindAtLessThanEqual(
                        getCurrentUserId(),
                        LocalDateTime.now(),
                        pageable
                )
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public ReminderResponse update(Long reminderId, ReminderRequest request) {
        Long userId = getCurrentUserId();
        Reminder reminder = findOwnedReminder(reminderId, userId);
        reminder.setSavedItem(findOwnedItem(request.getSavedItemId(), userId));
        reminder.setMessage(request.getMessage());
        reminder.setRemindAt(request.getRemindAt());
        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public ReminderResponse updateCompletion(Long reminderId, boolean completed) {
        Reminder reminder = findOwnedReminder(reminderId, getCurrentUserId());
        reminder.setCompleted(completed);
        return toResponse(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public void delete(Long reminderId) {
        reminderRepository.delete(findOwnedReminder(reminderId, getCurrentUserId()));
    }

    private SavedItems findOwnedItem(Long itemId, Long userId) {
        return savedItemsRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Saved item not found with id: " + itemId
                ));
    }

    private Reminder findOwnedReminder(Long reminderId, Long userId) {
        return reminderRepository.findByIdAndSavedItemUserId(reminderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reminder not found with id: " + reminderId
                ));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
        return user.getId();
    }

    private ReminderResponse toResponse(Reminder reminder) {
        ReminderResponse response = new ReminderResponse();
        response.setId(reminder.getId());
        response.setSavedItemId(reminder.getSavedItem().getId());
        response.setSavedItemTitle(reminder.getSavedItem().getTitle());
        response.setMessage(reminder.getMessage());
        response.setRemindAt(reminder.getRemindAt());
        response.setCompleted(reminder.isCompleted());
        response.setCreatedAt(reminder.getCreatedAt());
        return response;
    }
}
