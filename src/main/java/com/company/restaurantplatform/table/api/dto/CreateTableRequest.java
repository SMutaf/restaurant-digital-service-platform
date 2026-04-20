package com.company.restaurantplatform.table.api.dto;

import com.company.restaurantplatform.core.domain.enums.TableStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTableRequest(
        @NotBlank(message = "Table number is required")
        String tableNumber,
        @NotBlank(message = "Table name is required")
        String name,
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,
        TableStatus status
) {
}
