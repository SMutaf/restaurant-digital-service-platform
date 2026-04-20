package com.company.restaurantplatform.menu.mapper;

import com.company.restaurantplatform.core.domain.entity.MenuCategory;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.menu.api.dto.CategoryResponse;
import com.company.restaurantplatform.menu.api.dto.ProductResponse;

public final class MenuMapper {

    private MenuMapper() {
    }

    public static CategoryResponse toCategoryResponse(MenuCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getRestaurant().getId(),
                category.getName(),
                category.getDisplayOrder(),
                category.isActive()
        );
    }

    public static ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getRestaurant().getId(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isActive(),
                product.isVisibleToCustomer()
        );
    }
}
