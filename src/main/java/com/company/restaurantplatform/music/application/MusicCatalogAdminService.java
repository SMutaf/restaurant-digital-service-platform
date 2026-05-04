package com.company.restaurantplatform.music.application;

import com.company.restaurantplatform.core.domain.entity.Playlist;
import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.entity.Song;
import com.company.restaurantplatform.core.domain.enums.PlaylistStatus;
import com.company.restaurantplatform.core.repository.PlaylistRepository;
import com.company.restaurantplatform.core.repository.PlaylistSongRepository;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.core.repository.SongRepository;
import com.company.restaurantplatform.music.api.dto.AddPlaylistSongRequest;
import com.company.restaurantplatform.music.api.dto.CreatePlaylistRequest;
import com.company.restaurantplatform.music.api.dto.CreateSongRequest;
import com.company.restaurantplatform.music.api.dto.UpdateSongRequest;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicCatalogAdminService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantUserRepository restaurantUserRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;

    @Transactional
    public Song createSong(Long restaurantId, CreateSongRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);

        Song song = new Song();
        song.setRestaurant(restaurant);
        song.setTitle(request.title().trim());
        song.setArtist(request.artist());
        song.setDurationSeconds(request.durationSeconds());
        song.setSourceReference(request.sourceReference());
        song.setActive(true);
        return songRepository.save(song);
    }

    public List<Song> listSongs(Long restaurantId) {
        findRestaurant(restaurantId);
        return songRepository.findAllByRestaurantIdOrderByTitleAsc(restaurantId);
    }

    @Transactional
    public Song updateSong(Long restaurantId, Long songId, UpdateSongRequest request) {
        Song song = findSong(songId, restaurantId);
        song.setTitle(request.title().trim());
        song.setArtist(request.artist());
        song.setDurationSeconds(request.durationSeconds());
        song.setSourceReference(request.sourceReference());
        song.setActive(request.active());
        return songRepository.save(song);
    }

    @Transactional
    public Playlist createPlaylist(Long restaurantId, CreatePlaylistRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        RestaurantUser createdBy = findRestaurantUser(request.createdByRestaurantUserId(), restaurantId);

        Playlist playlist = new Playlist();
        playlist.setRestaurant(restaurant);
        playlist.setName(request.name().trim());
        playlist.setStatus(PlaylistStatus.ACTIVE);
        playlist.setCreatedByRestaurantUser(createdBy);
        return playlistRepository.save(playlist);
    }

    public List<Playlist> listPlaylists(Long restaurantId) {
        findRestaurant(restaurantId);
        return playlistRepository.findAllByRestaurantId(restaurantId);
    }

    @Transactional
    public PlaylistSong addSongToPlaylist(Long restaurantId, Long playlistId, AddPlaylistSongRequest request) {
        Playlist playlist = findPlaylist(playlistId, restaurantId);
        Song song = findSong(request.songId(), restaurantId);

        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, song.getId())) {
            throw new BusinessException("Song is already in playlist");
        }

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSong.setActive(true);
        playlistSong.setPosition(nextPlaylistSongPosition(playlistId));
        return playlistSongRepository.save(playlistSong);
    }

    public Playlist getPlaylist(Long restaurantId, Long playlistId) {
        return findPlaylist(playlistId, restaurantId);
    }

    public List<PlaylistSong> listPlaylistSongs(Long restaurantId, Long playlistId) {
        findPlaylist(playlistId, restaurantId);
        return playlistSongRepository.findAllByPlaylistIdOrderByPositionAsc(playlistId);
    }

    private int nextPlaylistSongPosition(Long playlistId) {
        return playlistSongRepository.findTopByPlaylistIdOrderByPositionDesc(playlistId)
                .map(playlistSong -> playlistSong.getPosition() + 1)
                .orElse(1);
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
    }

    private RestaurantUser findRestaurantUser(Long restaurantUserId, Long restaurantId) {
        return restaurantUserRepository.findByIdAndRestaurantId(restaurantUserId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant user not found"));
    }

    private Song findSong(Long songId, Long restaurantId) {
        return songRepository.findByIdAndRestaurantId(songId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Song not found"));
    }

    private Playlist findPlaylist(Long playlistId, Long restaurantId) {
        return playlistRepository.findByIdAndRestaurantId(playlistId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));
    }
}
