package com.company.restaurantplatform.ordering.application;

import com.company.restaurantplatform.core.domain.entity.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderPricingService {

    public BigDecimal calculateTotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
