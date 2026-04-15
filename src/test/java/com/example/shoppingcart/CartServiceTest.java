package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    private static final Logger LOGGER = Logger.getLogger(CartService.class.getName());
    private static final String INSERT_RECORD =
            "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)";
    private static final String INSERT_ITEM =
            "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?)";

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement recordStatement;

    @Mock
    private PreparedStatement itemStatement;

    @Mock
    private ResultSet generatedKeys;

    private TestLogHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestLogHandler();
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }

    @AfterEach
    void tearDown() {
        LOGGER.removeHandler(handler);
    }

    @Test
    void savesCartAndCommitsTransaction() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_RECORD, Statement.RETURN_GENERATED_KEYS)).thenReturn(recordStatement);
        when(connection.prepareStatement(INSERT_ITEM)).thenReturn(itemStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(7);

        CartService service = new CartService(connectionProvider);
        List<CartItem> items = List.of(
                new CartItem(new BigDecimal("10.00"), 2),
                new CartItem(new BigDecimal("5.50"), 1)
        );

        service.saveCart(2, 25.50, "en", items);

        verify(connection).setAutoCommit(false);
        verify(recordStatement).setInt(1, 2);
        verify(recordStatement).setDouble(2, 25.50);
        verify(recordStatement).setString(3, "en");
        verify(recordStatement).executeUpdate();
        verify(itemStatement, times(2)).addBatch();
        verify(itemStatement).executeBatch();
        verify(connection).commit();
        verify(connection, never()).rollback();
    }

    @Test
    void rollsBackAndLogsWhenSavingItemsFails() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_RECORD, Statement.RETURN_GENERATED_KEYS)).thenReturn(recordStatement);
        when(connection.prepareStatement(INSERT_ITEM)).thenReturn(itemStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(11);
        when(itemStatement.executeBatch()).thenThrow(new SQLException("insert failed"));

        CartService service = new CartService(connectionProvider);

        service.saveCart(1, 10.0, "en", List.of(new CartItem(new BigDecimal("10.00"), 1)));

        verify(connection).rollback();
        verify(connection, never()).commit();
        assertTrue(handler.contains(Level.SEVERE, "Failed to save cart"));
    }

    @Test
    void logsWhenConnectionCannotBeOpened() throws SQLException {
        when(connectionProvider.getConnection()).thenThrow(new SQLException("offline"));

        CartService service = new CartService(connectionProvider);

        service.saveCart(0, 0.0, "en", List.of());

        assertTrue(handler.contains(Level.SEVERE, "Failed to open a database connection"));
    }

    @Test
    void logsWhenGeneratedKeyIsMissing() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_RECORD, Statement.RETURN_GENERATED_KEYS)).thenReturn(recordStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        CartService service = new CartService(connectionProvider);

        service.saveCart(0, 0.0, "en", List.of());

        verify(connection).rollback();
        assertTrue(handler.contains(Level.SEVERE, "Failed to save cart"));
    }

    @Test
    void logsWhenRollbackFails() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_RECORD, Statement.RETURN_GENERATED_KEYS)).thenReturn(recordStatement);
        when(recordStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);
        doThrow(new SQLException("rollback failed")).when(connection).rollback();

        CartService service = new CartService(connectionProvider);

        service.saveCart(0, 0.0, "en", List.of());

        assertTrue(handler.contains(Level.SEVERE, "Failed to roll back cart save transaction"));
    }

    @Test
    void defaultConstructorCanBeCreatedWhenSystemPropertiesArePresent() {
        String previousUrl = System.getProperty(DatabaseConfig.URL_KEY);
        String previousUser = System.getProperty(DatabaseConfig.USER_KEY);
        String previousPassword = System.getProperty(DatabaseConfig.PASSWORD_KEY);

        try {
            System.setProperty(DatabaseConfig.URL_KEY, "jdbc:test");
            System.setProperty(DatabaseConfig.USER_KEY, "db-user");
            System.setProperty(DatabaseConfig.PASSWORD_KEY, "db-password");

            CartService service = new CartService();

            assertTrue(service instanceof CartService);
        } finally {
            restoreProperty(DatabaseConfig.URL_KEY, previousUrl);
            restoreProperty(DatabaseConfig.USER_KEY, previousUser);
            restoreProperty(DatabaseConfig.PASSWORD_KEY, previousPassword);
        }
    }

    private void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
            return;
        }
        System.setProperty(key, value);
    }
}
