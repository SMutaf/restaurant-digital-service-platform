package com.company.restaurantplatform.voting.api.publicapi;

import com.company.restaurantplatform.voting.api.dto.VoteRequest;
import com.company.restaurantplatform.voting.api.dto.VoteResponse;
import com.company.restaurantplatform.voting.api.dto.VotingRoundResponse;
import com.company.restaurantplatform.voting.application.VotingRoundService;
import com.company.restaurantplatform.voting.mapper.VotingMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/public/customer-sessions/{sessionToken}")
public class PublicVotingController {

    private final VotingRoundService votingRoundService;

    @GetMapping("/voting/current")
    public VotingRoundResponse getCurrentVoting(@PathVariable String sessionToken) {
        var round = votingRoundService.getCurrentRoundForSession(sessionToken);
        return VotingMapper.toRoundResponse(round, votingRoundService.getCandidates(round.getId()));
    }

    @PostMapping("/votes")
    @ResponseStatus(HttpStatus.CREATED)
    public VoteResponse castVote(@PathVariable String sessionToken, @Valid @RequestBody VoteRequest request) {
        var vote = votingRoundService.castVote(sessionToken, request);
        return new VoteResponse(vote.getVotingRound().getId(), vote.getSong().getId(), sessionToken);
    }
}
