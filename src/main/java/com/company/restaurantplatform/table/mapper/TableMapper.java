package com.company.restaurantplatform.table.mapper;

import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.table.api.dto.TableResponse;

public final class TableMapper {

    private TableMapper() {
    }

    public static TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getRestaurant().getId(),
                table.getTableNumber(),
                table.getName(),
                table.getCapacity(),
                table.getStatus()
        );
    }
}
