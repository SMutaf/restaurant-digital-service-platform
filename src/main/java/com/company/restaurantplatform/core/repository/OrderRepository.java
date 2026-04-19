package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByRestaurantTableIdAndStatusIn(Long restaurantTableId, Collection<OrderStatus> statuses);

    List<Order> findAllByRestaurantId(Long restaurantId);
}
