package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.OrderItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrderId(Long orderId);

    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);

    Optional<OrderItem> findByIdAndOrderId(Long orderItemId, Long orderId);
}
