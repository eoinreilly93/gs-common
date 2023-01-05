package com.shop.generic.common.valueobjects;

/**
 * A VO representing a request to purchase a product
 *
 * @param productId the ID of the product
 * @param quantity  the quantity of the product to be purchased
 */
public record PurchaseProductVO(int productId, int quantity) {

}
