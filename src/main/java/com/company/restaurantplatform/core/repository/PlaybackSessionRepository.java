package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import com.company.restaurantplatform.core.domain.enums.PlaybackSessionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackSessionRepository extends JpaRepository<PlaybackSession, Long> {

    Optional<PlaybackSession> findByRestaurantId(Long restaurantId);

    Optional<PlaybackSession> findByIdAndRestaurantId(Long playbackSessionId, Long restaurantId);

    Optional<PlaybackSession> findByRestaurantIdAndStatus(Long restaurantId, PlaybackSessionStatus status);
}
