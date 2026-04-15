package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocalizationServiceTest {

    private static final Logger LOGGER = Logger.getLogger(LocalizationService.class.getName());

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement statement;

    @Mock
    private ResultSet resultSet;

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
    void returnsLocalizationStringsFromResultSet() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("key")).thenReturn("ui.selectLanguage", "button.calculate");
        when(resultSet.getString("value")).thenReturn("Select language:", "Calculate total");

        LocalizationService service = new LocalizationService(connectionProvider);

        Map<String, String> strings = service.getStrings("en");

        assertEquals(
                Map.of(
                        "ui.selectLanguage", "Select language:",
                        "button.calculate", "Calculate total"
                ),
                strings
        );
        verify(statement).setString(1, "en");
    }

    @Test
    void returnsEmptyMapWhenNoRowsExist() throws SQLException {
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        LocalizationService service = new LocalizationService(connectionProvider);

        Map<String, String> strings = service.getStrings("fi");

        assertTrue(strings.isEmpty());
        verify(statement).setString(1, "fi");
    }

    @Test
    void returnsEmptyMapAndLogsWhenSqlExceptionOccurs() throws SQLException {
        when(connectionProvider.getConnection()).thenThrow(new SQLException("db down"));

        LocalizationService service = new LocalizationService(connectionProvider);

        Map<String, String> strings = service.getStrings("en");

        assertTrue(strings.isEmpty());
        assertTrue(handler.contains(Level.SEVERE, "Failed to load localization strings"));
    }
}
