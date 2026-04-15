package com.example.shoppingcart;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConnection implements ConnectionProvider {

    @FunctionalInterface
    interface SqlConnectionFactory {
        Connection connect(String url, String user, String password) throws SQLException;
    }

    private final DatabaseConfig config;
    private final SqlConnectionFactory connectionFactory;

    public DatabaseConnection() {
        this(DatabaseConfig.fromSystem());
    }

    DatabaseConnection(DatabaseConfig config) {
        this(config, DriverManager::getConnection);
    }

    DatabaseConnection(DatabaseConfig config, SqlConnectionFactory connectionFactory) {
        this.config = Objects.requireNonNull(config, "config");
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "connectionFactory");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionFactory.connect(config.url(), config.user(), config.password());
    }
}
