package com.company.restaurantplatform.playback.api.dto;

import jakarta.validation.constraints.NotNull;

public record StartPlaybackSessionRequest(
        @NotNull(message = "Playlist id is required")
        Long playlistId,
        @NotNull(message = "Current song id is required")
        Long currentSongId
) {
}
