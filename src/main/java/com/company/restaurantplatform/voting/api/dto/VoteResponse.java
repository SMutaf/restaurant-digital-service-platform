package com.company.restaurantplatform.voting.api.dto;

public record VoteResponse(
        Long votingRoundId,
        Long songId,
        String sessionToken
) {
}
