package com.company.restaurantplatform.qr.api.admin;

import com.company.restaurantplatform.qr.api.dto.QrCodeResponse;
import com.company.restaurantplatform.qr.application.QrAdminService;
import com.company.restaurantplatform.qr.mapper.QrMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/restaurants/{restaurantId}/tables/{tableId}/qr-codes")
public class QrAdminController {

    private final QrAdminService qrAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QrCodeResponse generateQrCode(@PathVariable Long restaurantId, @PathVariable Long tableId) {
        return QrMapper.toResponse(qrAdminService.generateQrCode(restaurantId, tableId));
    }

    @GetMapping("/active")
    public QrCodeResponse getActiveQrCode(@PathVariable Long restaurantId, @PathVariable Long tableId) {
        return QrMapper.toResponse(qrAdminService.getActiveQrCode(restaurantId, tableId));
    }
}
