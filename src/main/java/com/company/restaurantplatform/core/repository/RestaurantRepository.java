package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Restaurant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findBySlug(String slug);
}
