package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackSessionRepository extends JpaRepository<PlaybackSession, Long> {

    Optional<PlaybackSession> findByRestaurantId(Long restaurantId);
}
