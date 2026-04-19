package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByRestaurantIdAndVisibleToCustomerTrue(Long restaurantId);
}
