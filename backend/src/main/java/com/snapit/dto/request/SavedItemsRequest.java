package com.snapit.dto.request;

import com.snapit.enums.MemoryType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SavedItemsRequest {
    @NotBlank(message = "title should not be empty")
    private String title;
    private String sourceUrl;
    @NotNull(message = "Content type is required")
    @Enumerated(EnumType.STRING)
    private MemoryType contentType;
    private String description;
    private String extractedText;
    private String filePath;
    private String language;

    public SavedItemsRequest() {
    }

    public SavedItemsRequest(String title, String sourceUrl, MemoryType contentType, String description, String extractedText, String filePath, String language) {
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.contentType = contentType;
        this.description = description;
        this.extractedText = extractedText;
        this.filePath = filePath;
        this.language = language;
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
