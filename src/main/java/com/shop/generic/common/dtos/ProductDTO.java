package com.shop.generic.common.dtos;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.enums.StockStatus;
import java.math.BigDecimal;

public record ProductDTO(int id, String name, BigDecimal price, StockStatus stockStatus,
                         int stockCount) {

    public ProductDTO(final Product product) {
        this(product.getProductId(), product.getName(), product.getPrice(),
                product.getStockStatus(),
                product.getStockCount());
    }
}
