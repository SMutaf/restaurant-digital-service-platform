package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.CustomerSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Long> {

    Optional<CustomerSession> findBySessionToken(String sessionToken);
}
