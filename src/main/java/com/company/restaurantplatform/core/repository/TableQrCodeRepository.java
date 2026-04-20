package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.TableQrCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableQrCodeRepository extends JpaRepository<TableQrCode, Long> {

    Optional<TableQrCode> findByTokenAndActiveTrue(String token);

    Optional<TableQrCode> findByRestaurantTableIdAndActiveTrue(Long restaurantTableId);

    List<TableQrCode> findAllByRestaurantTableIdAndActiveTrue(Long restaurantTableId);
}
