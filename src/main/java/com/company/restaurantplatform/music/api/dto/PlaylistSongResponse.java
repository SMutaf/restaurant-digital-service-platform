package com.company.restaurantplatform.music.api.dto;

public record PlaylistSongResponse(
        Long id,
        Long songId,
        String title,
        String artist,
        Integer durationSeconds,
        Integer position,
        boolean active
) {
}
