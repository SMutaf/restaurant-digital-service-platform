package com.company.restaurantplatform.ordering.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderItemQuantityRequest(
        @NotNull(message = "Quantity is required")
        Integer quantity
) {
}
