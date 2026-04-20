package com.company.restaurantplatform.qr.mapper;

import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.qr.api.dto.PublicQrResolveResponse;
import com.company.restaurantplatform.qr.api.dto.QrCodeResponse;

public final class QrMapper {

    private QrMapper() {
    }

    public static QrCodeResponse toResponse(TableQrCode qrCode) {
        return new QrCodeResponse(
                qrCode.getId(),
                qrCode.getRestaurantTable().getRestaurant().getId(),
                qrCode.getRestaurantTable().getId(),
                qrCode.getRestaurantTable().getTableNumber(),
                qrCode.getToken(),
                qrCode.getQrImageUrl(),
                qrCode.isActive()
        );
    }

    public static PublicQrResolveResponse toPublicResolveResponse(TableQrCode qrCode) {
        return new PublicQrResolveResponse(
                qrCode.getRestaurantTable().getRestaurant().getId(),
                qrCode.getRestaurantTable().getRestaurant().getName(),
                qrCode.getRestaurantTable().getId(),
                qrCode.getRestaurantTable().getTableNumber(),
                qrCode.getToken()
        );
    }
}
