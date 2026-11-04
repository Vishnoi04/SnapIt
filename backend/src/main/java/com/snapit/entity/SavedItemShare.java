package com.snapit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "saved_item_shares",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_saved_item_share_recipient",
                columnNames = {"saved_item_id", "recipient_user_id"}
        )
)
public class SavedItemShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "saved_item_id", nullable = false)
    private SavedItems savedItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipient;

    @Column(name = "shared_at", nullable = false)
    private LocalDateTime sharedAt;

    public Long getId() {
        return id;
    }

    public SavedItems getSavedItem() {
        return savedItem;
    }

    public void setSavedItem(SavedItems savedItem) {
        this.savedItem = savedItem;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    @PrePersist
    public void onCreate() {
        if (sharedAt == null) {
            sharedAt = LocalDateTime.now();
        }
    }
}
