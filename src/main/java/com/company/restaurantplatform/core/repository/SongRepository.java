package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Song;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByRestaurantIdAndActiveTrue(Long restaurantId);

    List<Song> findAllByRestaurantIdOrderByTitleAsc(Long restaurantId);

    Optional<Song> findByIdAndRestaurantId(Long songId, Long restaurantId);
}
