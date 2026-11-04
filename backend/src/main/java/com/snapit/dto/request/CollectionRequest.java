package com.snapit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CollectionRequest {
    @NotBlank(message = "Collection name is required.")
    @Size(max = 100, message = "Collection name must not exceed 100 characters.")
    private String name;

    @Size(max = 1000, message = "Collection description must not exceed 1000 characters.")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
