package com.snapit.service;

import com.snapit.dto.request.ReminderRequest;
import com.snapit.dto.response.ReminderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReminderService {
    ReminderResponse create(ReminderRequest request);

    Page<ReminderResponse> getAll(Boolean completed, Pageable pageable);

    Page<ReminderResponse> getDue(Pageable pageable);

    ReminderResponse update(Long reminderId, ReminderRequest request);

    ReminderResponse updateCompletion(Long reminderId, boolean completed);

    void delete(Long reminderId);
}
