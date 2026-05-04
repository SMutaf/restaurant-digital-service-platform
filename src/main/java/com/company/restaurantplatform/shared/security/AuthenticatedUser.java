package com.company.restaurantplatform.shared.security;

public record AuthenticatedUser(
        Long userId,
        String email
) {
}
