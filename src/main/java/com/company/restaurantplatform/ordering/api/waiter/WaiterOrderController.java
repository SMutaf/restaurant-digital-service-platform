package com.company.restaurantplatform.ordering.api.waiter;

import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.ordering.api.dto.AddOrderItemRequest;
import com.company.restaurantplatform.ordering.api.dto.CreateOrderRequest;
import com.company.restaurantplatform.ordering.api.dto.OrderResponse;
import com.company.restaurantplatform.ordering.api.dto.UpdateOrderItemQuantityRequest;
import com.company.restaurantplatform.ordering.api.dto.UpdateOrderStatusRequest;
import com.company.restaurantplatform.ordering.application.OrderApplicationService;
import com.company.restaurantplatform.ordering.mapper.OrderMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/waiter/restaurants/{restaurantId}/orders")
public class WaiterOrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @PathVariable Long restaurantId,
            @RequestParam Long waiterRestaurantUserId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        Order order = orderApplicationService.createOrder(restaurantId, waiterRestaurantUserId, request.tableId());
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(order.getId()));
    }

    @GetMapping
    public List<OrderResponse> listOrders(
            @PathVariable Long restaurantId,
            @RequestParam Long waiterRestaurantUserId
    ) {
        return orderApplicationService.listWaiterOrders(restaurantId, waiterRestaurantUserId)
                .stream()
                .map(order -> OrderMapper.toResponse(order, orderApplicationService.getOrderItems(order.getId())))
                .toList();
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long restaurantId, @PathVariable Long orderId) {
        Order order = orderApplicationService.getOrder(restaurantId, orderId);
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(orderId));
    }

    @PostMapping("/{orderId}/items")
    public OrderResponse addOrderItem(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestParam Long waiterRestaurantUserId,
            @Valid @RequestBody AddOrderItemRequest request
    ) {
        Order order = orderApplicationService.addOrderItem(restaurantId, waiterRestaurantUserId, orderId, request);
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(orderId));
    }

    @PutMapping("/{orderId}/items/{orderItemId}")
    public OrderResponse updateOrderItemQuantity(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long orderItemId,
            @RequestParam Long waiterRestaurantUserId,
            @Valid @RequestBody UpdateOrderItemQuantityRequest request
    ) {
        Order order = orderApplicationService.updateOrderItemQuantity(
                restaurantId,
                waiterRestaurantUserId,
                orderId,
                orderItemId,
                request.quantity()
        );
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(orderId));
    }

    @PostMapping("/{orderId}/submit")
    public OrderResponse submitOrder(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @RequestParam Long waiterRestaurantUserId
    ) {
        Order order = orderApplicationService.submitOrder(restaurantId, waiterRestaurantUserId, orderId);
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(orderId));
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        Order order = orderApplicationService.transitionOrderStatus(restaurantId, orderId, request.status());
        return OrderMapper.toResponse(order, orderApplicationService.getOrderItems(orderId));
    }
}
