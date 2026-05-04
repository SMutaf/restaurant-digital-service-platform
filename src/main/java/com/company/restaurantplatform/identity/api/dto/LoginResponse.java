package com.company.restaurantplatform.identity.api.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        Long userId,
        String email,
        String fullName,
        List<AccessibleRestaurantResponse> restaurants
) {
}
