package com.example.shoppingcart;

import java.util.Locale;
import java.util.Map;

public final class LanguageSelector {
    private static final Map<String, Locale> SUPPORTED_LOCALES = Map.of(
            "en", Locale.US,
            "fi", Locale.forLanguageTag("fi-FI"),
            "sv", Locale.forLanguageTag("sv-SE"),
            "ja", Locale.JAPAN
    );

    private LanguageSelector() {
    }

    public static Locale resolveLocale(String input) {
        if (input == null) {
            return Locale.US;
        }

        return SUPPORTED_LOCALES.getOrDefault(input.trim().toLowerCase(Locale.ROOT), Locale.US);
    }
}
