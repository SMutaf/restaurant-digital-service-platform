package com.company.restaurantplatform.playback.api.dto;

import com.company.restaurantplatform.core.domain.enums.PlaybackQueueItemStatus;

public record PlaybackQueueItemResponse(
        Long id,
        Long songId,
        String title,
        String artist,
        Integer position,
        String sourceType,
        Long sourceReferenceId,
        PlaybackQueueItemStatus status
) {
}
