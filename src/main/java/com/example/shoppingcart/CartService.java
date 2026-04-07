package com.example.shoppingcart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CartService {

    public void saveCart(int totalItems, double totalCost, String language, List<CartItem> items) {
        String insertRecord = "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
        String insertItem = "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            int recordId;
            try (PreparedStatement stmt = conn.prepareStatement(insertRecord, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, totalItems);
                stmt.setDouble(2, totalCost);
                stmt.setString(3, language);
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    keys.next();
                    recordId = keys.getInt(1);
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(insertItem)) {
                for (int i = 0; i < items.size(); i++) {
                    CartItem item = items.get(i);
                    double subtotal = item.price().multiply(new java.math.BigDecimal(item.quantity())).doubleValue();
                    stmt.setInt(1, recordId);
                    stmt.setInt(2, i + 1);
                    stmt.setDouble(3, item.price().doubleValue());
                    stmt.setInt(4, item.quantity());
                    stmt.setDouble(5, subtotal);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("CartService: " + e.getMessage());
        }
    }
}
