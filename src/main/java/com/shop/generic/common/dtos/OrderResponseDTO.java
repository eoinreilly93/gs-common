package com.shop.generic.common.dtos;

import com.shop.generic.common.enums.OrderStatus;
import java.util.UUID;

public record OrderResponseDTO(UUID orderId, OrderStatus status) {

}
