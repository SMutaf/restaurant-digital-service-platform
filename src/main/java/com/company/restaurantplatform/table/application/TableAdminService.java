package com.company.restaurantplatform.table.application;

import com.company.restaurantplatform.core.domain.entity.Restaurant;
import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.enums.TableStatus;
import com.company.restaurantplatform.core.repository.RestaurantRepository;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import com.company.restaurantplatform.table.api.dto.CreateTableRequest;
import com.company.restaurantplatform.table.api.dto.UpdateTableRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TableAdminService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    @Transactional
    public RestaurantTable createTable(Long restaurantId, CreateTableRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);

        RestaurantTable table = new RestaurantTable();
        table.setRestaurant(restaurant);
        table.setTableNumber(request.tableNumber());
        table.setName(request.name());
        table.setCapacity(request.capacity());
        table.setStatus(request.status() == null ? TableStatus.ACTIVE : request.status());
        return restaurantTableRepository.save(table);
    }

    public List<RestaurantTable> listTables(Long restaurantId) {
        findRestaurant(restaurantId);
        return restaurantTableRepository.findAllByRestaurantIdOrderByTableNumberAsc(restaurantId);
    }

    @Transactional
    public RestaurantTable updateTable(Long restaurantId, Long tableId, UpdateTableRequest request) {
        RestaurantTable table = restaurantTableRepository.findByIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Table not found"));

        table.setName(request.name());
        table.setCapacity(request.capacity());
        table.setStatus(request.status());
        return restaurantTableRepository.save(table);
    }

    public RestaurantTable getTable(Long restaurantId, Long tableId) {
        findRestaurant(restaurantId);
        return restaurantTableRepository.findByIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Table not found"));
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));
    }
}
