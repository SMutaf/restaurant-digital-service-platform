package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaybackQueue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackQueueRepository extends JpaRepository<PlaybackQueue, Long> {

    Optional<PlaybackQueue> findByRestaurantId(Long restaurantId);
}
