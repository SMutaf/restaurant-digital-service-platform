package com.company.restaurantplatform.qr.api.dto;

public record PublicQrResolveResponse(
        Long restaurantId,
        String restaurantName,
        Long tableId,
        String tableNumber,
        String token
) {
}
