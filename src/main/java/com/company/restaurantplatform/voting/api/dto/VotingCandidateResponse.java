package com.company.restaurantplatform.voting.api.dto;

public record VotingCandidateResponse(
        Long songId,
        String title,
        String artist,
        Short candidateNo,
        long voteCount
) {
}
