package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueueItem;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueItemStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackQueueItemRepository extends JpaRepository<PlaybackQueueItem, Long> {

    List<PlaybackQueueItem> findAllByPlaybackQueueIdOrderByPositionAsc(Long playbackQueueId);

    Optional<PlaybackQueueItem> findTopByPlaybackQueueIdOrderByPositionDesc(Long playbackQueueId);

    Optional<PlaybackQueueItem> findFirstByPlaybackQueueIdAndStatusOrderByPositionAsc(
            Long playbackQueueId,
            PlaybackQueueItemStatus status
    );
}
