package com.company.restaurantplatform.music.api.dto;

import com.company.restaurantplatform.core.domain.enums.PlaylistStatus;
import java.util.List;

public record PlaylistResponse(
        Long id,
        Long restaurantId,
        String name,
        PlaylistStatus status,
        Long createdByRestaurantUserId,
        List<PlaylistSongResponse> songs
) {
}
