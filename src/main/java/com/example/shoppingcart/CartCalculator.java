package com.example.shoppingcart;

import java.math.BigDecimal;
import java.util.List;

public class CartCalculator {

    public BigDecimal calculateItemTotal(BigDecimal price, int quantity) {
        validateInputs(price, quantity);
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal calculateCartTotal(List<CartItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("Items cannot be null");
        }

        return items.stream()
                .map(item -> calculateItemTotal(item.price(), item.quantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateInputs(BigDecimal price, int quantity) {
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
