package com.company.restaurantplatform.playback.api.dto;

import jakarta.validation.constraints.NotNull;

public record ManualQueueItemRequest(
        @NotNull(message = "Song id is required")
        Long songId
) {
}
