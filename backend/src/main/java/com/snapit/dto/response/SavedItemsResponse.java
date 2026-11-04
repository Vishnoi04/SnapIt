package com.snapit.dto.response;

import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class SavedItemsResponse {
    private Long id;
    private String title;
    private String sourceUrl;
    @Enumerated(EnumType.STRING)
    private MemoryType contentType;
    private String description;
    private String extractedText;
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;
    private boolean favorite;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavedItemsResponse() {
    }

    public SavedItemsResponse(Long id, String title, String sourceUrl, MemoryType contentType, String description,
                              String extractedText, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.contentType = contentType;
        this.description = description;
        this.extractedText = extractedText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public MemoryType getContentType() {
        return contentType;
    }

    public void setContentType(MemoryType contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
