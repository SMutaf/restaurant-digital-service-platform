package com.company.restaurantplatform.ordering.api.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull(message = "Table id is required")
        Long tableId
) {
}
