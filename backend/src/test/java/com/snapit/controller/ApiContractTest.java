package com.snapit.controller;

import com.snapit.dto.request.ReminderRequest;
import com.snapit.dto.request.ShareSavedItemRequest;
import com.snapit.dto.response.PublicProfileResponse;
import com.snapit.dto.response.ReminderResponse;
import com.snapit.dto.response.ShareResponse;
import com.snapit.dto.response.SubscriptionResponse;
import com.snapit.enums.SubscriptionPlan;
import com.snapit.exception.GlobalExceptionHandler;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.service.ProfileService;
import com.snapit.service.ReminderService;
import com.snapit.service.ShareService;
import com.snapit.service.SubscriptionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.mock;

class ApiContractTest {
    private final ReminderService reminderService = mock(ReminderService.class);
    private final ShareService shareService = mock(ShareService.class);
    private final ProfileService profileService = mock(ProfileService.class);
    private final SubscriptionService subscriptionService = mock(SubscriptionService.class);
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new ReminderController(reminderService),
                        new ShareController(shareService),
                        new ProfileController(profileService),
                        new SubscriptionController(subscriptionService)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.destroy();
    }

    @Test
    void reminderCreateReturnsValidatedJsonContract() throws Exception {
        ReminderResponse response = new ReminderResponse();
        response.setId(8L);
        response.setSavedItemId(3L);
        response.setMessage("Review this");
        response.setRemindAt(LocalDateTime.now().plusDays(1));
        when(reminderService.create(any(ReminderRequest.class))).thenReturn(response);
        String request = """
                {
                  "savedItemId": 3,
                  "remindAt": "%s",
                  "message": "Review this"
                }
                """.formatted(LocalDateTime.now().plusDays(2).withNano(0));

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(8))
                .andExpect(jsonPath("$.savedItemId").value(3))
                .andExpect(jsonPath("$.message").value("Review this"));
    }

    @Test
    void dueRemindersHavePagedJsonContract() throws Exception {
        when(reminderService.getDue(any(PageRequest.class))).thenReturn(Page.empty(PageRequest.of(0, 10)));

        mockMvc.perform(get("/api/reminders/due"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(reminderService).getDue(any(PageRequest.class));
    }

    @Test
    void invalidReminderRequestUsesStandardValidationErrorShape() throws Exception {
        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "savedItemId": null,
                                  "remindAt": "2020-01-01T00:00:00",
                                  "message": "Review this"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.savedItemId").exists())
                .andExpect(jsonPath("$.fieldErrors.remindAt").exists());
    }

    @Test
    void shareCreateReturnsShareContract() throws Exception {
        ShareResponse response = new ShareResponse();
        response.setId(12L);
        response.setSavedItemId(3L);
        response.setRecipientUsername("reader");
        when(shareService.share(any(ShareSavedItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/shares")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "savedItemId": 3,
                                  "recipientUsername": "reader"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.savedItemId").value(3))
                .andExpect(jsonPath("$.recipientUsername").value("reader"));
    }

    @Test
    void subscriptionAndProfileResponsesExposeOnlyContractFields() throws Exception {
        SubscriptionResponse subscription = new SubscriptionResponse();
        subscription.setPlan(SubscriptionPlan.FREE);
        PublicProfileResponse profile = new PublicProfileResponse();
        profile.setUsername("reader");
        profile.setMemberSince(LocalDateTime.of(2026, 1, 1, 0, 0));
        when(subscriptionService.getCurrentSubscription()).thenReturn(subscription);
        when(profileService.getProfile("reader")).thenReturn(profile);

        mockMvc.perform(get("/api/subscriptions/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plan").value("FREE"))
                .andExpect(jsonPath("$.expiresAt").doesNotExist());
        mockMvc.perform(get("/api/profiles/reader"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("reader"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void notFoundErrorsKeepStandardContract() throws Exception {
        when(profileService.getProfile("missing"))
                .thenThrow(new ResourceNotFoundException("Profile not found."));

        mockMvc.perform(get("/api/profiles/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Profile not found."))
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }
}
