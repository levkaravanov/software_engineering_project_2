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

    @Test
    void fallsBackToEnglishForNullInput() {
        Locale locale = LanguageSelector.resolveLocale(null);

        assertEquals(Locale.US, locale);
    }

    @Test
    void resolvesTrimmedCaseInsensitiveInput() {
        Locale locale = LanguageSelector.resolveLocale("  FI ");

        assertEquals(Locale.forLanguageTag("fi-FI"), locale);
    }

    @Test
    void resolvesArabicLocale() {
        Locale locale = LanguageSelector.resolveLocale("ar");

        assertEquals(Locale.forLanguageTag("ar-AR"), locale);
    }

    @Test
    void resolvesLocaleByValidIndex() {
        Locale locale = LanguageSelector.localeByIndex(2);

        assertEquals(Locale.forLanguageTag("sv-SE"), locale);
    }

    @Test
    void fallsBackToEnglishForNegativeIndex() {
        Locale locale = LanguageSelector.localeByIndex(-1);

        assertEquals(Locale.US, locale);
    }

    @Test
    void fallsBackToEnglishForOutOfBoundsIndex() {
        Locale locale = LanguageSelector.localeByIndex(99);

        assertEquals(Locale.US, locale);
    }
}
