package com.shop.generic.common.dtos;

import java.util.List;

public record OrderCreationDTO(List<PurchaseProductDTO> purchaseProductDTOS, String city) {
    //TODO: Replace city with a user details object containing address, name etc.
}
