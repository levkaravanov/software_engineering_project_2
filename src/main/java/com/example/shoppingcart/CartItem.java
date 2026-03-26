package com.example.shoppingcart;

import java.math.BigDecimal;

public record CartItem(BigDecimal price, int quantity) {

    public CartItem {
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}
