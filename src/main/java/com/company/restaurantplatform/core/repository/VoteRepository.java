package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Vote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    long countByVotingRoundIdAndSongId(Long votingRoundId, Long songId);

    List<Vote> findAllByVotingRoundId(Long votingRoundId);
}
