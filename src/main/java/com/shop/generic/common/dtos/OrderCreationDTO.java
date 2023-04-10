package com.shop.generic.common.dtos;

import java.util.List;

public record OrderCreationDTO(List<PurchaseProductDTO> purchaseProductDTOS) {
    //TODO: Add additional fields such as buyer name, address etc. when that is implemented
}
