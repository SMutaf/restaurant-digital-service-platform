package com.company.restaurantplatform.playback.mapper;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueueItem;
import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import com.company.restaurantplatform.playback.api.dto.PlaybackQueueItemResponse;
import com.company.restaurantplatform.playback.api.dto.PlaybackSessionResponse;
import java.util.List;

public final class PlaybackMapper {

    private PlaybackMapper() {
    }

    public static PlaybackSessionResponse toSessionResponse(PlaybackSession session, List<PlaybackQueueItem> queueItems) {
        return new PlaybackSessionResponse(
                session.getId(),
                session.getRestaurant().getId(),
                session.getPlaylist().getId(),
                session.getCurrentSong() != null ? session.getCurrentSong().getId() : null,
                session.getCurrentSong() != null ? session.getCurrentSong().getTitle() : null,
                session.getCurrentSongStartedAt(),
                session.getCurrentSongEndsAt(),
                session.getVotingGraceEndsAt(),
                session.getStatus(),
                queueItems.stream().map(PlaybackMapper::toQueueItemResponse).toList()
        );
    }

    public static PlaybackQueueItemResponse toQueueItemResponse(PlaybackQueueItem item) {
        return new PlaybackQueueItemResponse(
                item.getId(),
                item.getSong().getId(),
                item.getSong().getTitle(),
                item.getSong().getArtist(),
                item.getPosition(),
                item.getSourceType(),
                item.getSourceReferenceId(),
                item.getStatus()
        );
    }
}
