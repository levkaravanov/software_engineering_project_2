package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MainControllerConstructorTest {

    @Test
    void defaultConstructorCanBeCreatedWhenSystemPropertiesArePresent() {
        String previousUrl = System.getProperty(DatabaseConfig.URL_KEY);
        String previousUser = System.getProperty(DatabaseConfig.USER_KEY);
        String previousPassword = System.getProperty(DatabaseConfig.PASSWORD_KEY);

        try {
            System.setProperty(DatabaseConfig.URL_KEY, "jdbc:test");
            System.setProperty(DatabaseConfig.USER_KEY, "db-user");
            System.setProperty(DatabaseConfig.PASSWORD_KEY, "db-password");

            MainController controller = new MainController();

            assertTrue(controller instanceof MainController);
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
