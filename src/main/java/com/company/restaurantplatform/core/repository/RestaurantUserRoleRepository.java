package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.RestaurantUserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantUserRoleRepository extends JpaRepository<RestaurantUserRole, Long> {

    List<RestaurantUserRole> findAllByRestaurantUserId(Long restaurantUserId);
}
