package com.company.restaurantplatform.customer.api.dto;

import java.time.OffsetDateTime;

public record CustomerSessionResponse(
        Long id,
        Long restaurantId,
        Long tableId,
        String tableNumber,
        String sessionToken,
        OffsetDateTime expiresAt
) {
}
