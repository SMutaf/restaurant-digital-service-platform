package com.company.restaurantplatform.voting.mapper;

import com.company.restaurantplatform.core.domain.entity.VotingRound;
import com.company.restaurantplatform.voting.api.dto.VotingCandidateResponse;
import com.company.restaurantplatform.voting.api.dto.VotingRoundResponse;
import java.util.List;

public final class VotingMapper {

    private VotingMapper() {
    }

    public static VotingRoundResponse toRoundResponse(VotingRound votingRound, List<VotingCandidateResponse> candidates) {
        return new VotingRoundResponse(
                votingRound.getId(),
                votingRound.getRestaurant().getId(),
                votingRound.getPlaybackSession().getId(),
                votingRound.getPlaylist().getId(),
                votingRound.getStatus(),
                votingRound.getOpenedAt(),
                votingRound.getScheduledCloseAt(),
                votingRound.getClosedAt(),
                votingRound.getWinningSong() != null ? votingRound.getWinningSong().getId() : null,
                votingRound.isTieBreakApplied(),
                candidates
        );
    }
}
