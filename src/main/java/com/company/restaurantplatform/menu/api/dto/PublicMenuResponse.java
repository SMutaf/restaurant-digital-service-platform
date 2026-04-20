package com.company.restaurantplatform.menu.api.dto;

import java.util.List;

public record PublicMenuResponse(
        Long restaurantId,
        String restaurantName,
        Long tableId,
        String tableNumber,
        List<PublicMenuCategoryResponse> categories
) {
}
