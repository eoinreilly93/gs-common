package com.shop.generic.common.valueobjects;

import com.shop.generic.common.entities.Product;
import com.shop.generic.common.enums.StockStatus;
import java.math.BigDecimal;

public record ProductVO(String name, BigDecimal price, StockStatus stockStatus, int stockCount) {

    public ProductVO(final Product product) {
        this(product.getName(), product.getPrice(), product.getStockStatus(),
                product.getStockCount());
    }
}
