package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.VotingRound;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingRoundRepository extends JpaRepository<VotingRound, Long> {

    List<VotingRound> findAllByRestaurantIdOrderByOpenedAtDesc(Long restaurantId);
}
