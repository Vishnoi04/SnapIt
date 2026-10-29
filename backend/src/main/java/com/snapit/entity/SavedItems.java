package com.snapit.entity;

import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_items")
public class SavedItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String sourceUrl;
    @Enumerated(EnumType.STRING)
    private MemoryType contentType;
    private String description;
    private String extractedText;
    private String filePath;
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;
    private String language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SavedItems() {
    }

    public SavedItems(Long id, String title, String sourceUrl, MemoryType contentType,
                      String description, String extractedText, String filePath, LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.contentType = contentType;
        this.description = description;
        this.extractedText = extractedText;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
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

    @PrePersist
    public void onCreate(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        createdAt = currentDateTime;
        updatedAt = currentDateTime;
    }
    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
