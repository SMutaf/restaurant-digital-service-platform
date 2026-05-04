package com.company.restaurantplatform.music.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateSongRequest(
        @NotBlank(message = "Title is required")
        String title,
        String artist,
        @Min(value = 1, message = "Duration must be at least 1 second")
        Integer durationSeconds,
        String sourceReference
) {
}
