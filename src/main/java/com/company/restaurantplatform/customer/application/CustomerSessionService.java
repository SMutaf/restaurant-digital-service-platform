package com.company.restaurantplatform.customer.application;

import com.company.restaurantplatform.core.domain.entity.CustomerSession;
import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import com.company.restaurantplatform.core.domain.enums.CustomerSessionStatus;
import com.company.restaurantplatform.core.repository.CustomerSessionRepository;
import com.company.restaurantplatform.qr.application.PublicQrResolveService;
import com.company.restaurantplatform.shared.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerSessionService {

    private static final long SESSION_HOURS = 6;

    private final PublicQrResolveService publicQrResolveService;
    private final CustomerSessionRepository customerSessionRepository;

    @Transactional
    public CustomerSession createSession(String qrToken) {
        TableQrCode qrCode = publicQrResolveService.resolve(qrToken);

        CustomerSession session = new CustomerSession();
        session.setRestaurant(qrCode.getRestaurantTable().getRestaurant());
        session.setRestaurantTable(qrCode.getRestaurantTable());
        session.setTableQrCode(qrCode);
        session.setSessionToken(UUID.randomUUID().toString());
        session.setStatus(CustomerSessionStatus.ACTIVE);
        session.setExpiresAt(OffsetDateTime.now().plusHours(SESSION_HOURS));
        session.setLastSeenAt(OffsetDateTime.now());
        return customerSessionRepository.save(session);
    }

    @Transactional
    public CustomerSession getActiveSession(String sessionToken) {
        CustomerSession session = customerSessionRepository.findBySessionTokenAndStatus(
                        sessionToken,
                        CustomerSessionStatus.ACTIVE
                )
                .orElseThrow(() -> new BusinessException("Customer session not found"));

        if (session.getExpiresAt().isBefore(OffsetDateTime.now())) {
            session.setStatus(CustomerSessionStatus.EXPIRED);
            customerSessionRepository.save(session);
            throw new BusinessException("Customer session has expired");
        }

        session.setLastSeenAt(OffsetDateTime.now());
        return customerSessionRepository.save(session);
    }
}
