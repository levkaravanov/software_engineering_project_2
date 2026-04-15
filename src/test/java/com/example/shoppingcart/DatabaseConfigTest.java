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

    @Test
    void rejectsBlankConstructorValue() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new DatabaseConfig("   ", "user", "password")
        );

        assertEquals("Missing required database setting: DB_URL", error.getMessage());
    }

    @Test
    void loadsValuesFromSystemProperties() {
        String previousUrl = System.getProperty(DatabaseConfig.URL_KEY);
        String previousUser = System.getProperty(DatabaseConfig.USER_KEY);
        String previousPassword = System.getProperty(DatabaseConfig.PASSWORD_KEY);

        try {
            System.setProperty(DatabaseConfig.URL_KEY, "jdbc:system");
            System.setProperty(DatabaseConfig.USER_KEY, "system-user");
            System.setProperty(DatabaseConfig.PASSWORD_KEY, "system-password");

            DatabaseConfig config = DatabaseConfig.fromSystem();

            assertEquals("jdbc:system", config.url());
            assertEquals("system-user", config.user());
            assertEquals("system-password", config.password());
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
