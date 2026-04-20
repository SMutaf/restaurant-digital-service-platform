package com.company.restaurantplatform.menu.api.dto;

public record CategoryResponse(
        Long id,
        Long restaurantId,
        String name,
        Integer displayOrder,
        boolean active
) {
}
