package com.company.restaurantplatform.menu.api.dto;

import java.util.List;

public record PublicMenuCategoryResponse(
        Long id,
        String name,
        Integer displayOrder,
        List<ProductResponse> products
) {
}
