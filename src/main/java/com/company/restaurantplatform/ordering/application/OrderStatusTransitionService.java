package com.company.restaurantplatform.ordering.application;

import com.company.restaurantplatform.core.domain.entity.Order;
import com.company.restaurantplatform.core.domain.enums.OrderStatus;
import com.company.restaurantplatform.shared.exception.BusinessException;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusTransitionService {

    public void submit(Order order) {
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessException("Only draft orders can be submitted");
        }

        order.setStatus(OrderStatus.RECEIVED);
        order.setSubmittedAt(OffsetDateTime.now());
    }

    public void transition(Order order, OrderStatus targetStatus) {
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.DRAFT && targetStatus == OrderStatus.RECEIVED) {
            submit(order);
            return;
        }

        if (currentStatus == OrderStatus.RECEIVED && targetStatus == OrderStatus.PREPARING) {
            order.setStatus(targetStatus);
            return;
        }

        if (currentStatus == OrderStatus.PREPARING && targetStatus == OrderStatus.READY) {
            order.setStatus(targetStatus);
            return;
        }

        if (currentStatus == OrderStatus.READY && targetStatus == OrderStatus.DELIVERED_TO_TABLE) {
            order.setStatus(targetStatus);
            return;
        }

        throw new BusinessException("Invalid order status transition");
    }
}
