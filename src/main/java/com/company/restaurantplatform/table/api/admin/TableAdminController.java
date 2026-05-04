package com.company.restaurantplatform.table.api.admin;

import com.company.restaurantplatform.table.api.dto.CreateTableRequest;
import com.company.restaurantplatform.table.api.dto.TableResponse;
import com.company.restaurantplatform.table.api.dto.UpdateTableRequest;
import com.company.restaurantplatform.table.application.TableAdminService;
import com.company.restaurantplatform.table.mapper.TableMapper;
import com.company.restaurantplatform.shared.security.AuthenticatedUser;
import com.company.restaurantplatform.shared.security.RestaurantAccessGuard;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/restaurants/{restaurantId}/tables")
public class TableAdminController {

    private final TableAdminService tableAdminService;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TableResponse createTable(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @Valid @RequestBody CreateTableRequest request
    ) {
        restaurantAccessGuard.requireRestaurantAdmin(authenticatedUser.userId(), restaurantId);
        return TableMapper.toResponse(tableAdminService.createTable(restaurantId, request));
    }

    @GetMapping
    public List<TableResponse> listTables(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId
    ) {
        restaurantAccessGuard.requireRestaurantAdmin(authenticatedUser.userId(), restaurantId);
        return tableAdminService.listTables(restaurantId)
                .stream()
                .map(TableMapper::toResponse)
                .toList();
    }

    @GetMapping("/{tableId}")
    public TableResponse getTable(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId
    ) {
        restaurantAccessGuard.requireRestaurantAdmin(authenticatedUser.userId(), restaurantId);
        return TableMapper.toResponse(tableAdminService.getTable(restaurantId, tableId));
    }

    @PutMapping("/{tableId}")
    public TableResponse updateTable(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long restaurantId,
            @PathVariable Long tableId,
            @Valid @RequestBody UpdateTableRequest request
    ) {
        restaurantAccessGuard.requireRestaurantAdmin(authenticatedUser.userId(), restaurantId);
        return TableMapper.toResponse(tableAdminService.updateTable(restaurantId, tableId, request));
    }
}
