package com.company.restaurantplatform.ordering.api.dto;

import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long restaurantId,
        Long tableId,
        String tableNumber,
        Long waiterRestaurantUserId,
        OrderStatus status,
        BigDecimal totalAmount,
        OffsetDateTime submittedAt,
        List<OrderItemResponse> items
) {
}
