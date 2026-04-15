package com.example.shoppingcart;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public record DatabaseConfig(String url, String user, String password) {

    static final String URL_KEY = "DB_URL";
    static final String USER_KEY = "DB_USER";
    static final String PASSWORD_KEY = "DB_PASSWORD";

    public DatabaseConfig {
        url = requireValue(URL_KEY, url);
        user = requireValue(USER_KEY, user);
        password = requireValue(PASSWORD_KEY, password);
    }

    public static DatabaseConfig fromSystem() {
        return fromSources(System.getenv(), systemProperties());
    }

    static DatabaseConfig fromSources(Map<String, String> environment, Map<String, String> systemProperties) {
        return new DatabaseConfig(
                readSetting(URL_KEY, environment, systemProperties),
                readSetting(USER_KEY, environment, systemProperties),
                readSetting(PASSWORD_KEY, environment, systemProperties)
        );
    }

    private static Map<String, String> systemProperties() {
        Properties properties = System.getProperties();
        return properties.stringPropertyNames().stream()
                .collect(Collectors.toMap(name -> name, properties::getProperty));
    }

    private static String readSetting(String key, Map<String, String> environment, Map<String, String> systemProperties) {
        String fromProperty = normalize(systemProperties.get(key));
        if (fromProperty != null) {
            return fromProperty;
        }

        String fromEnvironment = normalize(environment.get(key));
        if (fromEnvironment != null) {
            return fromEnvironment;
        }

        throw new IllegalStateException("Missing required database setting: " + key);
    }

    private static String requireValue(String key, String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Missing required database setting: " + key);
        }
        return normalized;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
