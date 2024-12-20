package com.shop.generic.common.dtos;

import java.math.BigDecimal;

/**
 * A VO representing a request to purchase a product
 *
 * @param productId the ID of the product
 * @param quantity  the quantity of the product to be purchased
 * @param price     the price of the product
 */
public record PurchaseProductDTO(int productId, int quantity, BigDecimal price) {

}
