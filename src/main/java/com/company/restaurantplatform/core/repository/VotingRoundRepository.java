package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.VotingRound;
import com.company.restaurantplatform.core.domain.enums.VotingRoundStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRoundRepository extends JpaRepository<VotingRound, Long> {

    List<VotingRound> findAllByRestaurantIdOrderByOpenedAtDesc(Long restaurantId);

    Optional<VotingRound> findByIdAndRestaurantId(Long votingRoundId, Long restaurantId);

    Optional<VotingRound> findFirstByRestaurantIdAndStatusOrderByOpenedAtDesc(Long restaurantId, VotingRoundStatus status);
}
