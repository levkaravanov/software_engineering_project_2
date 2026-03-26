package com.example.shoppingcart;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ShoppingCartApplication {
    private static final String BUNDLE_NAME = "MessagesBundle";

    private final CartCalculator cartCalculator;

    public ShoppingCartApplication() {
        this(new CartCalculator());
    }

    public ShoppingCartApplication(CartCalculator cartCalculator) {
        this.cartCalculator = cartCalculator;
    }

    public static void main(String[] args) {
        new ShoppingCartApplication().run();
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            Locale locale = selectLocale(scanner);
            ResourceBundle messages = ResourceBundle.getBundle(BUNDLE_NAME, locale);

            int itemCount = readNonNegativeInt(scanner, messages.getString("prompt.itemCount"), messages);
            List<CartItem> items = new ArrayList<>();

            for (int i = 1; i <= itemCount; i++) {
                System.out.printf(messages.getString("label.itemNumber"), i);
                BigDecimal price = readNonNegativeDecimal(scanner, messages.getString("prompt.price"), messages);
                int quantity = readNonNegativeInt(scanner, messages.getString("prompt.quantity"), messages);
                items.add(new CartItem(price, quantity));
            }

            BigDecimal totalCost = cartCalculator.calculateCartTotal(items);
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);
            String formattedTotal = numberFormat.format(totalCost);
            System.out.printf("%s %s%n", messages.getString("message.totalCost"), formattedTotal);
        }
    }

    private Locale selectLocale(Scanner scanner) {
        System.out.println("Select language / Valitse kieli / Välj språk / 言語を選択してください: en, fi, sv, ja");
        String input = scanner.nextLine();
        return LanguageSelector.resolveLocale(input);
    }

    private int readNonNegativeInt(Scanner scanner, String prompt, ResourceBundle messages) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    throw new NumberFormatException("Negative value");
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println(messages.getString("error.nonNegativeInteger"));
            }
        }
    }

    private BigDecimal readNonNegativeDecimal(Scanner scanner, String prompt, ResourceBundle messages) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.signum() < 0) {
                    throw new NumberFormatException("Negative value");
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println(messages.getString("error.nonNegativeDecimal"));
            }
        }
    }
}
