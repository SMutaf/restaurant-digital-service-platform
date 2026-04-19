package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantUserRepository extends JpaRepository<RestaurantUser, Long> {

    Optional<RestaurantUser> findByRestaurantIdAndUserId(Long restaurantId, Long userId);
}
