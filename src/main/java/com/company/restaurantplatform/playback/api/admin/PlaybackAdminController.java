package com.company.restaurantplatform.playback.api.admin;

import com.company.restaurantplatform.playback.api.dto.ManualQueueItemRequest;
import com.company.restaurantplatform.playback.api.dto.PlaybackQueueItemResponse;
import com.company.restaurantplatform.playback.api.dto.PlaybackSessionResponse;
import com.company.restaurantplatform.playback.api.dto.StartPlaybackSessionRequest;
import com.company.restaurantplatform.playback.application.PlaybackAdminService;
import com.company.restaurantplatform.playback.mapper.PlaybackMapper;
import com.company.restaurantplatform.shared.security.AuthenticatedUser;
import com.company.restaurantplatform.shared.security.RestaurantAccessGuard;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/admin/restaurants/{restaurantId}/playback")
public class PlaybackAdminController {

    private final PlaybackAdminService playbackAdminService;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaybackSessionResponse startSession(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @Valid @RequestBody StartPlaybackSessionRequest request
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var session = playbackAdminService.startSession(restaurantId, request);
        return PlaybackMapper.toSessionResponse(session, playbackAdminService.getQueueItems(restaurantId));
    }

    @GetMapping("/sessions/current")
    public PlaybackSessionResponse getCurrentSession(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var session = playbackAdminService.getCurrentSession(restaurantId);
        return PlaybackMapper.toSessionResponse(session, playbackAdminService.getQueueItems(restaurantId));
    }

    @GetMapping("/queue")
    public List<PlaybackQueueItemResponse> getQueue(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        return playbackAdminService.getQueueItems(restaurantId)
                .stream()
                .map(PlaybackMapper::toQueueItemResponse)
                .toList();
    }

    @PostMapping("/queue/items")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaybackQueueItemResponse addManualQueueItem(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @Valid @RequestBody ManualQueueItemRequest request
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        return PlaybackMapper.toQueueItemResponse(playbackAdminService.addManualQueueItem(restaurantId, request));
    }

    @PostMapping("/advance")
    public PlaybackSessionResponse advanceToNextQueuedSong(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var session = playbackAdminService.advanceToNextQueuedSong(restaurantId);
        return PlaybackMapper.toSessionResponse(session, playbackAdminService.getQueueItems(restaurantId));
    }
}
