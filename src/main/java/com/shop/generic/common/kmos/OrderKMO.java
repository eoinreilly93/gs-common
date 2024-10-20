package com.shop.generic.common.kmos;

import com.shop.generic.common.entities.Order;
import com.shop.generic.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a Kafka Message Object for an Order
 *
 * @param orderId     the order id
 * @param price       the total price of the order
 * @param productIds  the list of products being purchased
 * @param orderStatus the status of the order
 */
public record OrderKMO(UUID orderId, BigDecimal price, String productIds,
                       OrderStatus orderStatus, String city) {

    public OrderKMO(final Order order) {
        this(order.getOrderId(), order.getPrice(), order.getProductIds(),
                order.getStatus(), order.getCity());
    }
}
