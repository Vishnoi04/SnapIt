package com.snapit.dto.request;

import jakarta.validation.constraints.NotNull;

public class FavoriteRequest {
    @NotNull(message = "Favorite state is required.")
    private Boolean favorite;

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
