package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class LanguageSelectorTest {

    @Test
    void fallsBackToEnglishForUnknownLanguage() {
        Locale locale = LanguageSelector.resolveLocale("de");

        assertEquals(Locale.US, locale);
    }

    @Test
    void resolvesJapaneseLocale() {
        Locale locale = LanguageSelector.resolveLocale("ja");

        assertEquals(Locale.JAPAN, locale);
    }
}
