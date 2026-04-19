package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Song;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByRestaurantIdAndActiveTrue(Long restaurantId);
}
