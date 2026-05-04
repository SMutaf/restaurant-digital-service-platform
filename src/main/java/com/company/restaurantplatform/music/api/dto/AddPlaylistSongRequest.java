package com.company.restaurantplatform.music.api.dto;

import jakarta.validation.constraints.NotNull;

public record AddPlaylistSongRequest(
        @NotNull(message = "Song id is required")
        Long songId
) {
}
