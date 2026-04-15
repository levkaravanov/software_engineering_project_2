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

    @Test
    void defaultConstructorUsesSystemConfiguration() throws SQLException {
        String previousUrl = System.getProperty(DatabaseConfig.URL_KEY);
        String previousUser = System.getProperty(DatabaseConfig.USER_KEY);
        String previousPassword = System.getProperty(DatabaseConfig.PASSWORD_KEY);

        try {
            System.setProperty(DatabaseConfig.URL_KEY, "jdbc:test");
            System.setProperty(DatabaseConfig.USER_KEY, "db-user");
            System.setProperty(DatabaseConfig.PASSWORD_KEY, "db-password");

            Connection expectedConnection = Mockito.mock(Connection.class);
            DatabaseConnection connectionProvider = new DatabaseConnection(
                    DatabaseConfig.fromSystem(),
                    (url, user, password) -> {
                        org.junit.jupiter.api.Assertions.assertEquals("jdbc:test", url);
                        org.junit.jupiter.api.Assertions.assertEquals("db-user", user);
                        org.junit.jupiter.api.Assertions.assertEquals("db-password", password);
                        return expectedConnection;
                    }
            );

            Connection actualConnection = connectionProvider.getConnection();

            assertSame(expectedConnection, actualConnection);
            assertSame(DatabaseConnection.class, new DatabaseConnection().getClass());
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
