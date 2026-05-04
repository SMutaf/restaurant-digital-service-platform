package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Playlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    List<Playlist> findAllByRestaurantId(Long restaurantId);

    Optional<Playlist> findByIdAndRestaurantId(Long playlistId, Long restaurantId);
}
