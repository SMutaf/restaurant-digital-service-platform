package com.company.restaurantplatform.voting.api.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull(message = "Song id is required")
        Long songId
) {
}
