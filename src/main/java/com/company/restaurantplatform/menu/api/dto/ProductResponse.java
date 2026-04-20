package com.company.restaurantplatform.menu.api.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        Long restaurantId,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        boolean active,
        boolean visibleToCustomer
) {
}
