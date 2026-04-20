package com.company.restaurantplatform.qr.application;

import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.repository.TableQrCodeRepository;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicQrResolveService {

    private final TableQrCodeRepository tableQrCodeRepository;

    public TableQrCode resolve(String token) {
        return tableQrCodeRepository.findByTokenAndActiveTrue(token)
                .orElseThrow(() -> new NotFoundException("QR code not found"));
    }
}
