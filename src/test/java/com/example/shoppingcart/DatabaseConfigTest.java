package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

    @Test
    void loadsValuesFromEnvironmentWhenSystemPropertiesAreMissing() {
        DatabaseConfig config = DatabaseConfig.fromSources(
                Map.of(
                        DatabaseConfig.URL_KEY, " jdbc:mariadb://localhost:3306/cart ",
                        DatabaseConfig.USER_KEY, " dbuser ",
                        DatabaseConfig.PASSWORD_KEY, " secret "
                ),
                Map.of()
        );

        assertEquals("jdbc:mariadb://localhost:3306/cart", config.url());
        assertEquals("dbuser", config.user());
        assertEquals("secret", config.password());
    }

    @Test
    void prefersSystemPropertiesOverEnvironmentValues() {
        DatabaseConfig config = DatabaseConfig.fromSources(
                Map.of(
                        DatabaseConfig.URL_KEY, "jdbc:env",
                        DatabaseConfig.USER_KEY, "env-user",
                        DatabaseConfig.PASSWORD_KEY, "env-pass"
                ),
                Map.of(
                        DatabaseConfig.URL_KEY, "jdbc:property",
                        DatabaseConfig.USER_KEY, "property-user",
                        DatabaseConfig.PASSWORD_KEY, "property-pass"
                )
        );

        assertEquals("jdbc:property", config.url());
        assertEquals("property-user", config.user());
        assertEquals("property-pass", config.password());
    }

    @Test
    void rejectsMissingRequiredSetting() {
        IllegalStateException error = assertThrows(
                IllegalStateException.class,
                () -> DatabaseConfig.fromSources(
                        Map.of(
                                DatabaseConfig.URL_KEY, "jdbc:test",
                                DatabaseConfig.USER_KEY, "user"
                        ),
                        Map.of()
                )
        );

        assertEquals("Missing required database setting: DB_PASSWORD", error.getMessage());
    }
}
