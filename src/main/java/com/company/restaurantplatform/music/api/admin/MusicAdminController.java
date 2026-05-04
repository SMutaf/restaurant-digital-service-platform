package com.company.restaurantplatform.music.api.admin;

import com.company.restaurantplatform.music.api.dto.AddPlaylistSongRequest;
import com.company.restaurantplatform.music.api.dto.CreatePlaylistRequest;
import com.company.restaurantplatform.music.api.dto.CreateSongRequest;
import com.company.restaurantplatform.music.api.dto.PlaylistResponse;
import com.company.restaurantplatform.music.api.dto.SongResponse;
import com.company.restaurantplatform.music.api.dto.UpdateSongRequest;
import com.company.restaurantplatform.music.application.MusicCatalogAdminService;
import com.company.restaurantplatform.music.mapper.MusicMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/admin/restaurants/{restaurantId}/music")
public class MusicAdminController {

    private final MusicCatalogAdminService musicCatalogAdminService;

    @PostMapping("/songs")
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponse createSong(@PathVariable Long restaurantId, @Valid @RequestBody CreateSongRequest request) {
        return MusicMapper.toSongResponse(musicCatalogAdminService.createSong(restaurantId, request));
    }

    @GetMapping("/songs")
    public List<SongResponse> listSongs(@PathVariable Long restaurantId) {
        return musicCatalogAdminService.listSongs(restaurantId)
                .stream()
                .map(MusicMapper::toSongResponse)
                .toList();
    }

    @PutMapping("/songs/{songId}")
    public SongResponse updateSong(
            @PathVariable Long restaurantId,
            @PathVariable Long songId,
            @Valid @RequestBody UpdateSongRequest request
    ) {
        return MusicMapper.toSongResponse(musicCatalogAdminService.updateSong(restaurantId, songId, request));
    }

    @PostMapping("/playlists")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistResponse createPlaylist(
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreatePlaylistRequest request
    ) {
        var playlist = musicCatalogAdminService.createPlaylist(restaurantId, request);
        return MusicMapper.toPlaylistResponse(playlist, musicCatalogAdminService.listPlaylistSongs(restaurantId, playlist.getId()));
    }

    @GetMapping("/playlists")
    public List<PlaylistResponse> listPlaylists(@PathVariable Long restaurantId) {
        return musicCatalogAdminService.listPlaylists(restaurantId)
                .stream()
                .map(playlist -> MusicMapper.toPlaylistResponse(
                        playlist,
                        musicCatalogAdminService.listPlaylistSongs(restaurantId, playlist.getId())
                ))
                .toList();
    }

    @PostMapping("/playlists/{playlistId}/songs")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistResponse addSongToPlaylist(
            @PathVariable Long restaurantId,
            @PathVariable Long playlistId,
            @Valid @RequestBody AddPlaylistSongRequest request
    ) {
        musicCatalogAdminService.addSongToPlaylist(restaurantId, playlistId, request);
        var playlist = musicCatalogAdminService.getPlaylist(restaurantId, playlistId);
        return MusicMapper.toPlaylistResponse(playlist, musicCatalogAdminService.listPlaylistSongs(restaurantId, playlistId));
    }

    @GetMapping("/playlists/{playlistId}")
    public PlaylistResponse getPlaylist(@PathVariable Long restaurantId, @PathVariable Long playlistId) {
        var playlist = musicCatalogAdminService.getPlaylist(restaurantId, playlistId);
        return MusicMapper.toPlaylistResponse(playlist, musicCatalogAdminService.listPlaylistSongs(restaurantId, playlistId));
    }
}
