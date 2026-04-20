package com.company.restaurantplatform.qr.api.publicapi;

import com.company.restaurantplatform.qr.api.dto.PublicQrResolveResponse;
import com.company.restaurantplatform.qr.application.PublicQrResolveService;
import com.company.restaurantplatform.qr.mapper.QrMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/qr")
public class PublicQrController {

    private final PublicQrResolveService publicQrResolveService;

    @GetMapping("/{token}")
    @Transactional
    public PublicQrResolveResponse resolve(@PathVariable String token) {
        return QrMapper.toPublicResolveResponse(publicQrResolveService.resolve(token));
    }
}
