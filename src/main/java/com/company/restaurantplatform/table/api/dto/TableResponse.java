package com.company.restaurantplatform.table.api.dto;

import com.company.restaurantplatform.core.domain.enums.TableStatus;

public record TableResponse(
        Long id,
        Long restaurantId,
        String tableNumber,
        String name,
        Integer capacity,
        TableStatus status
) {
}
