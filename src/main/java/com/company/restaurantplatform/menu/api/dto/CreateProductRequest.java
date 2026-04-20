package com.company.restaurantplatform.menu.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotNull(message = "Category id is required")
        Long categoryId,
        @NotBlank(message = "Product name is required")
        String name,
        String description,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        BigDecimal price,
        boolean active,
        boolean visibleToCustomer
) {
}
