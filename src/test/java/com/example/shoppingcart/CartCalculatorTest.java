package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class CartCalculatorTest {
    private final CartCalculator calculator = new CartCalculator();

    @Test
    void calculatesSingleItemTotal() {
        BigDecimal total = calculator.calculateItemTotal(new BigDecimal("19.99"), 3);

        assertEquals(new BigDecimal("59.97"), total);
    }

    @Test
    void calculatesCartTotalForMultipleItems() {
        List<CartItem> items = List.of(
                new CartItem(new BigDecimal("12.50"), 2),
                new CartItem(new BigDecimal("1.99"), 5),
                new CartItem(new BigDecimal("100.00"), 1)
        );

        BigDecimal total = calculator.calculateCartTotal(items);

        assertEquals(new BigDecimal("134.95"), total);
    }

    @Test
    void returnsZeroForEmptyCart() {
        BigDecimal total = calculator.calculateCartTotal(List.of());

        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void handlesLargeDecimalValues() {
        BigDecimal total = calculator.calculateItemTotal(new BigDecimal("123456789.1234"), 2);

        assertEquals(new BigDecimal("246913578.2468"), total);
    }

    @Test
    void rejectsNullPrice() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateItemTotal(null, 1)
        );

        assertEquals("Price cannot be null", error.getMessage());
    }

    @Test
    void rejectsNegativePrice() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateItemTotal(new BigDecimal("-0.01"), 1)
        );

        assertEquals("Price cannot be negative", error.getMessage());
    }

    @Test
    void rejectsNegativeQuantity() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateItemTotal(new BigDecimal("1.00"), -1)
        );

        assertEquals("Quantity cannot be negative", error.getMessage());
    }

    @Test
    void rejectsNullItemList() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateCartTotal(null)
        );

        assertEquals("Items cannot be null", error.getMessage());
    }
}
