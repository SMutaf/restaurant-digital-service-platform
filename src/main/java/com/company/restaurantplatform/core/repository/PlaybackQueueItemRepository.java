package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueueItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackQueueItemRepository extends JpaRepository<PlaybackQueueItem, Long> {

    List<PlaybackQueueItem> findAllByPlaybackQueueIdOrderByPositionAsc(Long playbackQueueId);
}
