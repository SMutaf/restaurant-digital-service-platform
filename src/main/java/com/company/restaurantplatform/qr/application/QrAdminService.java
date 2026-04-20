package com.company.restaurantplatform.qr.application;

import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.core.repository.TableQrCodeRepository;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrAdminService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TableQrCodeRepository tableQrCodeRepository;

    @Transactional
    public TableQrCode generateQrCode(Long restaurantId, Long tableId) {
        RestaurantTable table = restaurantTableRepository.findByIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        tableQrCodeRepository.findAllByRestaurantTableIdAndActiveTrue(table.getId())
                .forEach(existing -> existing.setActive(false));

        TableQrCode qrCode = new TableQrCode();
        qrCode.setRestaurantTable(table);
        qrCode.setToken(generateToken(table));
        qrCode.setQrImageUrl("/api/public/qr/" + qrCode.getToken());
        qrCode.setActive(true);
        return tableQrCodeRepository.save(qrCode);
    }

    public TableQrCode getActiveQrCode(Long restaurantId, Long tableId) {
        restaurantTableRepository.findByIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        return tableQrCodeRepository.findByRestaurantTableIdAndActiveTrue(tableId)
                .orElseThrow(() -> new NotFoundException("Active QR code not found"));
    }

    private String generateToken(RestaurantTable table) {
        return table.getRestaurant().getId()
                + "-"
                + table.getId()
                + "-"
                + UUID.randomUUID().toString().replace("-", "");
    }
}
