package com.company.restaurantplatform.music.mapper;

import com.company.restaurantplatform.core.domain.entity.Playlist;
import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import com.company.restaurantplatform.core.domain.entity.Song;
import com.company.restaurantplatform.music.api.dto.PlaylistResponse;
import com.company.restaurantplatform.music.api.dto.PlaylistSongResponse;
import com.company.restaurantplatform.music.api.dto.SongResponse;
import java.util.List;

public final class MusicMapper {

    private MusicMapper() {
    }

    public static SongResponse toSongResponse(Song song) {
        return new SongResponse(
                song.getId(),
                song.getRestaurant().getId(),
                song.getTitle(),
                song.getArtist(),
                song.getDurationSeconds(),
                song.getSourceReference(),
                song.isActive()
        );
    }

    public static PlaylistResponse toPlaylistResponse(Playlist playlist, List<PlaylistSong> playlistSongs) {
        return new PlaylistResponse(
                playlist.getId(),
                playlist.getRestaurant().getId(),
                playlist.getName(),
                playlist.getStatus(),
                playlist.getCreatedByRestaurantUser().getId(),
                playlistSongs.stream().map(MusicMapper::toPlaylistSongResponse).toList()
        );
    }

    public static PlaylistSongResponse toPlaylistSongResponse(PlaylistSong playlistSong) {
        return new PlaylistSongResponse(
                playlistSong.getId(),
                playlistSong.getSong().getId(),
                playlistSong.getSong().getTitle(),
                playlistSong.getSong().getArtist(),
                playlistSong.getSong().getDurationSeconds(),
                playlistSong.getPosition(),
                playlistSong.isActive()
        );
    }
}
