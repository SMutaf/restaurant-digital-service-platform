package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.VotingRoundCandidateSong;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRoundCandidateSongRepository extends JpaRepository<VotingRoundCandidateSong, Long> {

    List<VotingRoundCandidateSong> findAllByVotingRoundIdOrderByCandidateNoAsc(Long votingRoundId);
}
