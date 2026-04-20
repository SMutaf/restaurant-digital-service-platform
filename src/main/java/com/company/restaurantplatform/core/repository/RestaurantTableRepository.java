package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByRestaurantIdAndTableNumber(Long restaurantId, String tableNumber);

    Optional<RestaurantTable> findByIdAndRestaurantId(Long tableId, Long restaurantId);

    List<RestaurantTable> findAllByRestaurantIdOrderByTableNumberAsc(Long restaurantId);
}
