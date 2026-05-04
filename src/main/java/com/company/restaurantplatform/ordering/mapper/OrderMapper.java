package com.company.restaurantplatform.ordering.mapper;

import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.core.domain.entity.OrderItem;
import com.company.restaurantplatform.ordering.api.dto.OrderItemResponse;
import com.company.restaurantplatform.ordering.api.dto.OrderResponse;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order, List<OrderItem> items) {
        return new OrderResponse(
                order.getId(),
                order.getRestaurant().getId(),
                order.getRestaurantTable().getId(),
                order.getRestaurantTable().getTableNumber(),
                order.getCreatedByRestaurantUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getSubmittedAt(),
                items.stream().map(OrderMapper::toItemResponse).toList()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductNameSnapshot(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getNote()
        );
    }
}
