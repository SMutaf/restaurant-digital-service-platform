package com.company.restaurantplatform.voting.api.dto;

import com.company.restaurantplatform.core.domain.enums.VotingRoundStatus;
import java.time.OffsetDateTime;
import java.util.List;

public record VotingRoundResponse(
        Long id,
        Long restaurantId,
        Long playbackSessionId,
        Long playlistId,
        VotingRoundStatus status,
        OffsetDateTime openedAt,
        OffsetDateTime scheduledCloseAt,
        OffsetDateTime closedAt,
        Long winningSongId,
        boolean tieBreakApplied,
        List<VotingCandidateResponse> candidates
) {
}
