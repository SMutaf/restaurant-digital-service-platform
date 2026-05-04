package com.company.restaurantplatform.playback.api.dto;

import com.company.restaurantplatform.core.domain.enums.PlaybackSessionStatus;
import java.time.OffsetDateTime;
import java.util.List;

public record PlaybackSessionResponse(
        Long id,
        Long restaurantId,
        Long playlistId,
        Long currentSongId,
        String currentSongTitle,
        OffsetDateTime currentSongStartedAt,
        OffsetDateTime currentSongEndsAt,
        OffsetDateTime votingGraceEndsAt,
        PlaybackSessionStatus status,
        List<PlaybackQueueItemResponse> queueItems
) {
}
