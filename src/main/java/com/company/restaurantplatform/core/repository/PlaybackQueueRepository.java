package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueue;
import com.company.restaurantplatform.core.domain.enums.PlaybackQueueStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackQueueRepository extends JpaRepository<PlaybackQueue, Long> {

    Optional<PlaybackQueue> findByRestaurantId(Long restaurantId);

    Optional<PlaybackQueue> findByRestaurantIdAndStatus(Long restaurantId, PlaybackQueueStatus status);
}
