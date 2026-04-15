package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CartItemTest {

    @Test
    void createsValidCartItem() {
        CartItem item = new CartItem(new BigDecimal("9.99"), 2);

        assertEquals(new BigDecimal("9.99"), item.price());
        assertEquals(2, item.quantity());
    }

    @Test
    void rejectsNullPrice() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(null, 1)
        );

        assertEquals("Price cannot be null", error.getMessage());
    }

    @Test
    void rejectsNegativePrice() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(new BigDecimal("-1.00"), 1)
        );

        assertEquals("Price cannot be negative", error.getMessage());
    }

    @Test
    void rejectsNegativeQuantity() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new CartItem(new BigDecimal("1.00"), -1)
        );

        assertEquals("Quantity cannot be negative", error.getMessage());
    }
}
