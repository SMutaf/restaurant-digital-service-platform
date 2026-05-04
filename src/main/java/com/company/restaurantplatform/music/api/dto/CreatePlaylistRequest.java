package com.company.restaurantplatform.music.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePlaylistRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Created by restaurant user id is required")
        Long createdByRestaurantUserId
) {
}
