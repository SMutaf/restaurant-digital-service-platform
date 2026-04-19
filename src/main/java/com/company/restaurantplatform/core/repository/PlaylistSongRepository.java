package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findAllByPlaylistIdOrderByPositionAsc(Long playlistId);
}
