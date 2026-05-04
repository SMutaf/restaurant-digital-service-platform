package com.company.restaurantplatform.voting.application;

import com.company.restaurantplatform.core.domain.entity.CustomerSession;
import com.company.restaurantplatform.core.domain.entity.PlaybackSession;
import com.company.restaurantplatform.core.domain.entity.PlaylistSong;
import com.company.restaurantplatform.core.domain.entity.Song;
import com.company.restaurantplatform.core.domain.entity.Vote;
import com.company.restaurantplatform.core.domain.entity.VotingRound;
import com.company.restaurantplatform.core.domain.entity.VotingRoundCandidateSong;
import com.company.restaurantplatform.core.domain.enums.CandidateSelectionSource;
import com.company.restaurantplatform.core.domain.enums.VotingRoundCreatedByType;
import com.company.restaurantplatform.core.domain.enums.VotingRoundStatus;
import com.company.restaurantplatform.core.repository.PlaylistSongRepository;
import com.company.restaurantplatform.core.repository.VoteRepository;
import com.company.restaurantplatform.core.repository.VotingRoundCandidateSongRepository;
import com.company.restaurantplatform.core.repository.VotingRoundRepository;
import com.company.restaurantplatform.customer.application.CustomerSessionService;
import com.company.restaurantplatform.playback.application.PlaybackAdminService;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import com.company.restaurantplatform.voting.api.dto.VoteRequest;
import com.company.restaurantplatform.voting.api.dto.VotingCandidateResponse;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VotingRoundService {

    private final VotingRoundRepository votingRoundRepository;
    private final VotingRoundCandidateSongRepository votingRoundCandidateSongRepository;
    private final VoteRepository voteRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final CustomerSessionService customerSessionService;
    private final PlaybackAdminService playbackAdminService;

    @Transactional
    public VotingRound openAutomaticRound(Long restaurantId) {
        votingRoundRepository.findFirstByRestaurantIdAndStatusOrderByOpenedAtDesc(restaurantId, VotingRoundStatus.OPEN)
                .ifPresent(round -> {
                    throw new BusinessException("There is already an open voting round");
                });

        PlaybackSession playbackSession = playbackAdminService.getCurrentSession(restaurantId);
        List<PlaylistSong> availableSongs = playlistSongRepository
                .findAllByPlaylistIdAndActiveTrueOrderByPositionAsc(playbackSession.getPlaylist().getId())
                .stream()
                .filter(playlistSong -> playlistSong.getSong().isActive())
                .filter(playlistSong -> playbackSession.getCurrentSong() == null
                        || !playlistSong.getSong().getId().equals(playbackSession.getCurrentSong().getId()))
                .limit(2)
                .toList();

        if (availableSongs.size() < 2) {
            throw new BusinessException("At least two active playlist songs are required to open a voting round");
        }

        VotingRound votingRound = new VotingRound();
        votingRound.setRestaurant(playbackSession.getRestaurant());
        votingRound.setPlaybackSession(playbackSession);
        votingRound.setPlaylist(playbackSession.getPlaylist());
        votingRound.setStatus(VotingRoundStatus.OPEN);
        votingRound.setCreatedByType(VotingRoundCreatedByType.SYSTEM);
        votingRound.setScheduledCloseAt(playbackSession.getVotingGraceEndsAt());
        votingRound.setTieBreakApplied(false);
        votingRound = votingRoundRepository.save(votingRound);

        short candidateNo = 1;
        for (PlaylistSong playlistSong : availableSongs) {
            VotingRoundCandidateSong candidate = new VotingRoundCandidateSong();
            candidate.setVotingRound(votingRound);
            candidate.setSong(playlistSong.getSong());
            candidate.setCandidateNo(candidateNo++);
            candidate.setSelectionSource(CandidateSelectionSource.PLAYLIST_AUTO);
            votingRoundCandidateSongRepository.save(candidate);
        }

        return votingRound;
    }

    public VotingRound getCurrentRound(Long restaurantId) {
        return votingRoundRepository.findFirstByRestaurantIdAndStatusOrderByOpenedAtDesc(restaurantId, VotingRoundStatus.OPEN)
                .orElseThrow(() -> new NotFoundException("Open voting round not found"));
    }

    public VotingRound getCurrentRoundForSession(String sessionToken) {
        CustomerSession session = customerSessionService.getActiveSession(sessionToken);
        return getCurrentRound(session.getRestaurant().getId());
    }

    public List<VotingCandidateResponse> getCandidates(Long votingRoundId) {
        return votingRoundCandidateSongRepository.findAllByVotingRoundIdOrderByCandidateNoAsc(votingRoundId)
                .stream()
                .map(candidate -> new VotingCandidateResponse(
                        candidate.getSong().getId(),
                        candidate.getSong().getTitle(),
                        candidate.getSong().getArtist(),
                        candidate.getCandidateNo(),
                        voteRepository.countByVotingRoundIdAndSongId(votingRoundId, candidate.getSong().getId())
                ))
                .toList();
    }

    @Transactional
    public Vote castVote(String sessionToken, VoteRequest request) {
        CustomerSession session = customerSessionService.getActiveSession(sessionToken);
        VotingRound votingRound = getCurrentRound(session.getRestaurant().getId());

        if (votingRound.getScheduledCloseAt().isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Voting round is already closed");
        }

        if (voteRepository.existsByCustomerSessionIdAndVotingRoundId(session.getId(), votingRound.getId())) {
            throw new BusinessException("Customer session has already voted in this round");
        }

        Map<Long, VotingRoundCandidateSong> candidatesBySongId = votingRoundCandidateSongRepository
                .findAllByVotingRoundIdOrderByCandidateNoAsc(votingRound.getId())
                .stream()
                .collect(Collectors.toMap(candidate -> candidate.getSong().getId(), Function.identity()));

        VotingRoundCandidateSong candidate = candidatesBySongId.get(request.songId());
        if (candidate == null) {
            throw new BusinessException("Song is not available in the current voting round");
        }

        Vote vote = new Vote();
        vote.setVotingRound(votingRound);
        vote.setSong(candidate.getSong());
        vote.setCustomerSession(session);
        vote.setRestaurantTable(session.getRestaurantTable());
        return voteRepository.save(vote);
    }

    @Transactional
    public VotingRound resolveRound(Long restaurantId, Long votingRoundId) {
        VotingRound votingRound = votingRoundRepository.findByIdAndRestaurantId(votingRoundId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Voting round not found"));

        if (votingRound.getStatus() != VotingRoundStatus.OPEN) {
            throw new BusinessException("Only open voting rounds can be resolved");
        }

        List<VotingRoundCandidateSong> candidates = votingRoundCandidateSongRepository
                .findAllByVotingRoundIdOrderByCandidateNoAsc(votingRoundId);
        if (candidates.isEmpty()) {
            throw new BusinessException("Voting round does not have candidate songs");
        }

        Map<Long, Long> voteCounts = candidates.stream()
                .collect(Collectors.toMap(
                        candidate -> candidate.getSong().getId(),
                        candidate -> voteRepository.countByVotingRoundIdAndSongId(votingRoundId, candidate.getSong().getId())
                ));

        long highestVoteCount = voteCounts.values().stream().max(Comparator.naturalOrder()).orElse(0L);
        List<Song> winners = new ArrayList<>();
        for (VotingRoundCandidateSong candidate : candidates) {
            if (voteCounts.get(candidate.getSong().getId()) == highestVoteCount) {
                winners.add(candidate.getSong());
            }
        }

        Song winningSong;
        boolean tieBreakApplied = false;
        if (winners.size() == 1) {
            winningSong = winners.get(0);
        } else {
            tieBreakApplied = winners.size() > 1;
            winningSong = winners.get(ThreadLocalRandom.current().nextInt(winners.size()));
        }

        votingRound.setWinningSong(winningSong);
        votingRound.setTieBreakApplied(tieBreakApplied);
        votingRound.setStatus(VotingRoundStatus.RESOLVED);
        votingRound.setClosedAt(OffsetDateTime.now());
        VotingRound resolvedRound = votingRoundRepository.save(votingRound);

        playbackAdminService.enqueueVotingWinner(restaurantId, winningSong.getId(), resolvedRound.getId());
        return resolvedRound;
    }
}
