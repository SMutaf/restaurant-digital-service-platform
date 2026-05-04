package com.company.restaurantplatform.music.api.dto;

public record SongResponse(
        Long id,
        Long restaurantId,
        String title,
        String artist,
        Integer durationSeconds,
        String sourceReference,
        boolean active
) {
}
