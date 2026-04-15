package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertSame;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DatabaseConnectionTest {

    @Test
    void delegatesToConfiguredConnectionFactory() throws SQLException {
        DatabaseConfig config = new DatabaseConfig("jdbc:test", "db-user", "db-password");
        Connection expectedConnection = Mockito.mock(Connection.class);

        DatabaseConnection connectionProvider = new DatabaseConnection(
                config,
                (url, user, password) -> {
                    org.junit.jupiter.api.Assertions.assertEquals("jdbc:test", url);
                    org.junit.jupiter.api.Assertions.assertEquals("db-user", user);
                    org.junit.jupiter.api.Assertions.assertEquals("db-password", password);
                    return expectedConnection;
                }
        );

        Connection actualConnection = connectionProvider.getConnection();

        assertSame(expectedConnection, actualConnection);
    }
}
