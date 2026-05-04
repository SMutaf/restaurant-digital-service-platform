package com.company.restaurantplatform.ordering.api.dto;

import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
