package com.company.restaurantplatform.ordering.application;

import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.core.domain.entity.OrderItem;
import com.company.restaurantplatform.core.domain.entity.Product;
import com.company.restaurantplatform.core.domain.entity.RestaurantTable;
import com.company.restaurantplatform.core.domain.entity.RestaurantUser;
import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import com.company.restaurantplatform.core.repository.OrderItemRepository;
import com.company.restaurantplatform.core.repository.OrderRepository;
import com.company.restaurantplatform.core.repository.ProductRepository;
import com.company.restaurantplatform.core.repository.RestaurantTableRepository;
import com.company.restaurantplatform.core.repository.RestaurantUserRepository;
import com.company.restaurantplatform.ordering.api.dto.AddOrderItemRequest;
import com.company.restaurantplatform.shared.exception.BusinessException;
import com.company.restaurantplatform.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private static final List<OrderStatus> ACTIVE_ORDER_STATUSES = List.of(
            OrderStatus.DRAFT,
            OrderStatus.RECEIVED,
            OrderStatus.PREPARING,
            OrderStatus.READY
    );

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantUserRepository restaurantUserRepository;
    private final ProductRepository productRepository;
    private final OrderPricingService orderPricingService;
    private final OrderStatusTransitionService orderStatusTransitionService;

    @Transactional
    public Order createOrder(Long restaurantId, Long waiterRestaurantUserId, Long tableId) {
        RestaurantUser waiter = findRestaurantUser(waiterRestaurantUserId, restaurantId);
        RestaurantTable table = findTable(tableId, restaurantId);

        if (orderRepository.existsByRestaurantTableIdAndStatusIn(table.getId(), ACTIVE_ORDER_STATUSES)) {
            throw new BusinessException("There is already an active order for this table");
        }

        Order order = new Order();
        order.setRestaurant(table.getRestaurant());
        order.setRestaurantTable(table);
        order.setCreatedByRestaurantUser(waiter);
        order.setStatus(OrderStatus.DRAFT);
        order.setTotalAmount(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    @Transactional
    public Order addOrderItem(Long restaurantId, Long waiterRestaurantUserId, Long orderId, AddOrderItemRequest request) {
        Order order = findOwnedOrder(orderId, restaurantId, waiterRestaurantUserId);
        validateItemMutationAllowed(order);

        Product product = productRepository.findByIdAndRestaurantId(request.productId(), restaurantId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        OrderItem orderItem = orderItemRepository.findByOrderIdAndProductId(order.getId(), product.getId())
                .orElseGet(() -> {
                    OrderItem newItem = new OrderItem();
                    newItem.setOrder(order);
                    newItem.setProduct(product);
                    newItem.setProductNameSnapshot(product.getName());
                    newItem.setUnitPrice(product.getPrice());
                    newItem.setQuantity(0);
                    return newItem;
                });

        orderItem.setQuantity(orderItem.getQuantity() + request.quantity());
        orderItem.setNote(request.note());
        orderItem.setUnitPrice(product.getPrice());
        orderItemRepository.save(orderItem);

        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderItemQuantity(
            Long restaurantId,
            Long waiterRestaurantUserId,
            Long orderId,
            Long orderItemId,
            Integer quantity
    ) {
        Order order = findOwnedOrder(orderId, restaurantId, waiterRestaurantUserId);
        validateItemMutationAllowed(order);

        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(() -> new NotFoundException("Order item not found"));

        if (quantity <= 0) {
            orderItemRepository.delete(orderItem);
        } else {
            orderItem.setQuantity(quantity);
            orderItemRepository.save(orderItem);
        }

        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order submitOrder(Long restaurantId, Long waiterRestaurantUserId, Long orderId) {
        Order order = findOwnedOrder(orderId, restaurantId, waiterRestaurantUserId);

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        if (items.isEmpty()) {
            throw new BusinessException("Order must contain at least one item");
        }

        recalculateOrderTotal(order);
        orderStatusTransitionService.submit(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order transitionOrderStatus(Long restaurantId, Long orderId, OrderStatus targetStatus) {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        orderStatusTransitionService.transition(order, targetStatus);
        return orderRepository.save(order);
    }

    public Order getOrder(Long restaurantId, Long orderId) {
        return orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    public List<Order> listWaiterOrders(Long restaurantId, Long waiterRestaurantUserId) {
        findRestaurantUser(waiterRestaurantUserId, restaurantId);
        return orderRepository.findAllByCreatedByRestaurantUserIdAndRestaurantId(waiterRestaurantUserId, restaurantId);
    }

    private void recalculateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findAllByOrderId(order.getId());
        order.setTotalAmount(orderPricingService.calculateTotal(items));
    }

    private void validateItemMutationAllowed(Order order) {
        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.RECEIVED) {
            throw new BusinessException("Order items can only be changed while order is draft or received");
        }
    }

    private Order findOwnedOrder(Long orderId, Long restaurantId, Long waiterRestaurantUserId) {
        Order order = orderRepository.findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getCreatedByRestaurantUser().getId().equals(waiterRestaurantUserId)) {
            throw new BusinessException("Waiter can only manage own orders");
        }

        return order;
    }

    private RestaurantUser findRestaurantUser(Long restaurantUserId, Long restaurantId) {
        return restaurantUserRepository.findByIdAndRestaurantId(restaurantUserId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant user not found"));
    }

    private RestaurantTable findTable(Long tableId, Long restaurantId) {
        return restaurantTableRepository.findByIdAndRestaurantId(tableId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Table not found"));
    }
}
