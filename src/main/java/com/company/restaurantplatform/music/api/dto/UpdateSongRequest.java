package com.company.restaurantplatform.music.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSongRequest(
        @NotBlank(message = "Title is required")
        String title,
        String artist,
        @Min(value = 1, message = "Duration must be at least 1 second")
        Integer durationSeconds,
        String sourceReference,
        @NotNull(message = "Active flag is required")
        Boolean active
) {
}
