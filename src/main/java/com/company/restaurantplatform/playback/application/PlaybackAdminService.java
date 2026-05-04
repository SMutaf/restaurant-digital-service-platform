package com.company.restaurantplatform.playback.application;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueue;
import com.company.restaurantplatform.core.domain.entity.PlaybackQueueItem;
import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import com.company.restaurantplatform.core.domain.entity.Playlist;
import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.Song;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueItemStatus;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueStatus;
import com.company.restaurantplatform.core.domain.enums.PlaybackSessionStatus;
import com.company.restaurantplatform.core.repository.PlaybackQueueItemRepository;
import com.company.restaurantplatform.core.repository.PlaybackQueueRepository;
import com.company.restaurantplatform.core.repository.PlaybackSessionRepository;
import com.company.restaurantplatform.core.repository.PlaylistRepository;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.SongRepository;
import com.company.restaurantplatform.playback.api.dto.ManualQueueItemRequest;
import com.company.restaurantplatform.playback.api.dto.StartPlaybackSessionRequest;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaybackAdminService {

    private static final long VOTING_GRACE_SECONDS = 10;

    private final RestaurantRepository restaurantRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final PlaybackQueueRepository playbackQueueRepository;
    private final PlaybackQueueItemRepository playbackQueueItemRepository;
    private final PlaybackSessionRepository playbackSessionRepository;

    @Transactional
    public PlaybackSession startSession(Long restaurantId, StartPlaybackSessionRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        Playlist playlist = findPlaylist(request.playlistId(), restaurantId);
        Song currentSong = findSong(request.currentSongId(), restaurantId);

        PlaybackQueue playbackQueue = playbackQueueRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackQueueStatus.ACTIVE)
                .orElseGet(() -> {
                    PlaybackQueue newQueue = new PlaybackQueue();
                    newQueue.setRestaurant(restaurant);
                    newQueue.setStatus(PlaybackQueueStatus.ACTIVE);
                    return playbackQueueRepository.save(newQueue);
                });

        playbackSessionRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackSessionStatus.ACTIVE)
                .ifPresent(existing -> existing.setStatus(PlaybackSessionStatus.STOPPED));

        PlaybackSession session = playbackSessionRepository.findByRestaurantId(restaurantId)
                .orElseGet(PlaybackSession::new);
        session.setRestaurant(restaurant);
        session.setPlaylist(playlist);
        session.setPlaybackQueue(playbackQueue);
        session.setCurrentSong(currentSong);
        session.setStatus(PlaybackSessionStatus.ACTIVE);

        OffsetDateTime startedAt = OffsetDateTime.now();
        session.setCurrentSongStartedAt(startedAt);
        OffsetDateTime endsAt = startedAt.plusSeconds(currentSong.getDurationSeconds());
        session.setCurrentSongEndsAt(endsAt);
        session.setVotingGraceEndsAt(endsAt.plusSeconds(VOTING_GRACE_SECONDS));
        return playbackSessionRepository.save(session);
    }

    public PlaybackSession getCurrentSession(Long restaurantId) {
        return playbackSessionRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackSessionStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active playback session not found"));
    }

    public List<PlaybackQueueItem> getQueueItems(Long restaurantId) {
        PlaybackQueue queue = playbackQueueRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackQueueStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Playback queue not found"));
        return playbackQueueItemRepository.findAllByPlaybackQueueIdOrderByPositionAsc(queue.getId());
    }

    @Transactional
    public PlaybackQueueItem addManualQueueItem(Long restaurantId, ManualQueueItemRequest request) {
        PlaybackQueue queue = playbackQueueRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackQueueStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Playback queue not found"));
        Song song = findSong(request.songId(), restaurantId);
        return enqueueSong(queue, song, "MANUAL_ADMIN", null);
    }

    @Transactional
    public PlaybackSession advanceToNextQueuedSong(Long restaurantId) {
        PlaybackSession session = getCurrentSession(restaurantId);
        PlaybackQueueItem nextItem = playbackQueueItemRepository.findFirstByPlaybackQueueIdAndStatusOrderByPositionAsc(
                        session.getPlaybackQueue().getId(),
                        PlaybackQueueItemStatus.QUEUED
                )
                .orElseThrow(() -> new BusinessException("Playback queue is empty"));

        nextItem.setStatus(PlaybackQueueItemStatus.PLAYED);
        playbackQueueItemRepository.save(nextItem);

        OffsetDateTime startedAt = OffsetDateTime.now();
        session.setCurrentSong(nextItem.getSong());
        session.setCurrentSongStartedAt(startedAt);
        OffsetDateTime endsAt = startedAt.plusSeconds(nextItem.getSong().getDurationSeconds());
        session.setCurrentSongEndsAt(endsAt);
        session.setVotingGraceEndsAt(endsAt.plusSeconds(VOTING_GRACE_SECONDS));
        return playbackSessionRepository.save(session);
    }

    @Transactional
    public PlaybackQueueItem enqueueVotingWinner(Long restaurantId, Long songId, Long votingRoundId) {
        PlaybackQueue queue = playbackQueueRepository.findByRestaurantIdAndStatus(restaurantId, PlaybackQueueStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Playback queue not found"));
        Song song = findSong(songId, restaurantId);
        return enqueueSong(queue, song, "VOTING_WINNER", votingRoundId);
    }

    private PlaybackQueueItem enqueueSong(PlaybackQueue queue, Song song, String sourceType, Long sourceReferenceId) {
        PlaybackQueueItem item = new PlaybackQueueItem();
        item.setPlaybackQueue(queue);
        item.setSong(song);
        item.setSourceType(sourceType);
        item.setSourceReferenceId(sourceReferenceId);
        item.setPosition(nextQueuePosition(queue.getId()));
        item.setStatus(PlaybackQueueItemStatus.QUEUED);
        return playbackQueueItemRepository.save(item);
    }

    private int nextQueuePosition(Long playbackQueueId) {
        return playbackQueueItemRepository.findTopByPlaybackQueueIdOrderByPositionDesc(playbackQueueId)
                .map(item -> item.getPosition() + 1)
                .orElse(1);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
    }

    private Playlist findPlaylist(Long playlistId, Long restaurantId) {
        return playlistRepository.findByIdAndRestaurantId(playlistId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));
    }

    private Song findSong(Long songId, Long restaurantId) {
        return songRepository.findByIdAndRestaurantId(songId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Song not found"));
    }
}
