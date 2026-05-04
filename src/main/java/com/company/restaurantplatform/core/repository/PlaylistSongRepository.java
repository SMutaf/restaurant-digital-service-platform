package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findAllByPlaylistIdOrderByPositionAsc(Long playlistId);

    List<PlaylistSong> findAllByPlaylistIdAndActiveTrueOrderByPositionAsc(Long playlistId);

    boolean existsByPlaylistIdAndSongId(Long playlistId, Long songId);

    Optional<PlaylistSong> findTopByPlaylistIdOrderByPositionDesc(Long playlistId);
}
