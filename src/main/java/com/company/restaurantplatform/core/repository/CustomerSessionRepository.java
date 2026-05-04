package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.CustomerSession;
import com.company.restaurantplatform.core.domain.enums.CustomerSessionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {

    Optional<CustomerSession> findBySessionToken(String sessionToken);

    Optional<CustomerSession> findBySessionTokenAndStatus(String sessionToken, CustomerSessionStatus status);
}
