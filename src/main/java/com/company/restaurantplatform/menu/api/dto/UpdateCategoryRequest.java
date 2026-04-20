package com.company.restaurantplatform.menu.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,
        @NotNull(message = "Display order is required")
        @Min(value = 0, message = "Display order cannot be negative")
        Integer displayOrder,
        boolean active
) {
}
