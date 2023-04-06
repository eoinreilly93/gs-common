package com.shop.generic.common.dtos;

/**
 * A VO representing a request to purchase a product
 *
 * @param productId the ID of the product
 * @param quantity  the quantity of the product to be purchased
 */
public record PurchaseProductDTO(int productId, int quantity) {

}
