package com.company.restaurantplatform.customer.api.publicapi;

import com.company.restaurantplatform.customer.api.dto.CustomerSessionResponse;
import com.company.restaurantplatform.customer.application.CustomerSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class PublicCustomerSessionController {

    private final CustomerSessionService customerSessionService;

    @PostMapping("/qr/{token}/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public CustomerSessionResponse createSession(@PathVariable String token) {
        var session = customerSessionService.createSession(token);
        return new CustomerSessionResponse(
                session.getId(),
                session.getRestaurant().getId(),
                session.getRestaurantTable().getId(),
                session.getRestaurantTable().getTableNumber(),
                session.getSessionToken(),
                session.getExpiresAt()
        );
    }
}
