package com.company.restaurantplatform.voting.api.admin;

import com.company.restaurantplatform.voting.api.dto.VotingRoundResponse;
import com.company.restaurantplatform.voting.application.VotingRoundService;
import com.company.restaurantplatform.voting.mapper.VotingMapper;
import com.company.restaurantplatform.shared.security.AuthenticatedUser;
import com.company.restaurantplatform.shared.security.RestaurantAccessGuard;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/admin/restaurants/{restaurantId}/voting")
public class VotingAdminController {

    private final VotingRoundService votingRoundService;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @PostMapping("/rounds/open")
    @ResponseStatus(HttpStatus.CREATED)
    public VotingRoundResponse openRound(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var round = votingRoundService.openAutomaticRound(restaurantId);
        return VotingMapper.toRoundResponse(round, votingRoundService.getCandidates(round.getId()));
    }

    @GetMapping("/current")
    public VotingRoundResponse getCurrentRound(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var round = votingRoundService.getCurrentRound(restaurantId);
        return VotingMapper.toRoundResponse(round, votingRoundService.getCandidates(round.getId()));
    }

    @PostMapping("/rounds/{roundId}/resolve")
    public VotingRoundResponse resolveRound(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @PathVariable Long roundId
    ) {
        restaurantAccessGuard.requireMusicAccess(authenticatedUser.userId(), restaurantId);
        var round = votingRoundService.resolveRound(restaurantId, roundId);
        return VotingMapper.toRoundResponse(round, votingRoundService.getCandidates(round.getId()));
    }
}
