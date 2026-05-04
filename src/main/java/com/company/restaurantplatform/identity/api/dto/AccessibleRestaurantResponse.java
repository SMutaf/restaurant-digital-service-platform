package com.company.restaurantplatform.identity.api.dto;

import java.util.List;

public record AccessibleRestaurantResponse(
        Long restaurantId,
        String restaurantName,
        Long restaurantUserId,
        List<String> roles
) {
}
