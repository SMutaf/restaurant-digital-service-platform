package com.company.restaurantplatform.core.repository;

import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

    List<MenuCategory> findAllByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);
}
