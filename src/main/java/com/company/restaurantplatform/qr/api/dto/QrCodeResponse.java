package com.company.restaurantplatform.qr.api.dto;

public record QrCodeResponse(
        Long id,
        Long restaurantId,
        Long tableId,
        String tableNumber,
        String token,
        String qrImageUrl,
        boolean active
) {
}
