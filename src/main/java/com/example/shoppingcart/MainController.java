package com.example.shoppingcart;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private Label languageLabel;
    @FXML private ChoiceBox<String> languageChoice;
    @FXML private Label itemCountLabel;
    @FXML private TextField itemCountField;
    @FXML private Button generateButton;
    @FXML private VBox itemsContainer;
    @FXML private Button calculateButton;
    @FXML private Label totalLabel;
    @FXML private VBox root;

    private final CartCalculator calculator;
    private final LocalizationService localizationService;
    private final CartService cartService;
    private final List<ItemRow> rows = new ArrayList<>();
    private Map<String, String> messages = new HashMap<>();
    private Locale currentLocale = Locale.US;

    @FXML
    void initialize() {
        languageChoice.setItems(FXCollections.observableArrayList(
                "English", "Suomi", "Svenska", "\u65e5\u672c\u8a9e", "\u0627\u0644\u0639\u0631\u0628\u064a\u0629"));
        languageChoice.getSelectionModel().select(0);
        languageChoice.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldIndex, newIndex) -> {
                    currentLocale = LanguageSelector.localeByIndex(newIndex.intValue());
                    reloadMessages();
                });
        reloadMessages();
    }

    public MainController() {
        this(new CartCalculator(), new LocalizationService(), new CartService());
    }

    MainController(CartCalculator calculator, LocalizationService localizationService, CartService cartService) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.localizationService = Objects.requireNonNull(localizationService, "localizationService");
        this.cartService = Objects.requireNonNull(cartService, "cartService");
    }

    void bindView(
            Label languageLabel,
            ChoiceBox<String> languageChoice,
            Label itemCountLabel,
            TextField itemCountField,
            Button generateButton,
            VBox itemsContainer,
            Button calculateButton,
            Label totalLabel,
            VBox root
    ) {
        this.languageLabel = Objects.requireNonNull(languageLabel, "languageLabel");
        this.languageChoice = Objects.requireNonNull(languageChoice, "languageChoice");
        this.itemCountLabel = Objects.requireNonNull(itemCountLabel, "itemCountLabel");
        this.itemCountField = Objects.requireNonNull(itemCountField, "itemCountField");
        this.generateButton = Objects.requireNonNull(generateButton, "generateButton");
        this.itemsContainer = Objects.requireNonNull(itemsContainer, "itemsContainer");
        this.calculateButton = Objects.requireNonNull(calculateButton, "calculateButton");
        this.totalLabel = Objects.requireNonNull(totalLabel, "totalLabel");
        this.root = Objects.requireNonNull(root, "root");
    }

    private void reloadMessages() {
        messages = localizationService.getStrings(currentLocale.getLanguage());
        root.setNodeOrientation("ar".equals(currentLocale.getLanguage())
                ? NodeOrientation.RIGHT_TO_LEFT
                : NodeOrientation.LEFT_TO_RIGHT);
        languageLabel.setText(messages.getOrDefault("ui.selectLanguage", "Select language:"));
        itemCountLabel.setText(messages.getOrDefault("prompt.itemCount", "Number of items:"));
        generateButton.setText(messages.getOrDefault("button.enterItems", "Enter items"));
        calculateButton.setText(messages.getOrDefault("button.calculate", "Calculate total"));
        totalLabel.setText(messages.getOrDefault("message.totalCost", "Total cost:") + " ");
        for (ItemRow row : rows) {
            row.updateLabels(messages);
        }
    }

    @FXML
    void onGenerateItems() {
        itemsContainer.getChildren().clear();
        rows.clear();
        int count;
        try {
            count = Integer.parseInt(itemCountField.getText().trim());
            if (count < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            totalLabel.setText(messages.getOrDefault("error.nonNegativeInteger", "Please enter a non-negative whole number."));
            return;
        }
        for (int i = 1; i <= count; i++) {
            ItemRow row = new ItemRow(i, messages);
            rows.add(row);
            itemsContainer.getChildren().add(row.getNode());
        }
        totalLabel.setText(messages.getOrDefault("message.totalCost", "Total cost:") + " ");
    }

    @FXML
    void onCalculate() {
        try {
            List<CartItem> items = new ArrayList<>();
            for (ItemRow row : rows) {
                items.add(row.toCartItem());
            }
            BigDecimal total = calculator.calculateCartTotal(items);
            NumberFormat fmt = NumberFormat.getNumberInstance(currentLocale);
            fmt.setMinimumFractionDigits(2);
            fmt.setMaximumFractionDigits(2);
            totalLabel.setText(messages.getOrDefault("message.totalCost", "Total cost:") + " " + fmt.format(total));
            cartService.saveCart(items.size(), total.doubleValue(), currentLocale.getLanguage(), items);
        } catch (IllegalArgumentException ex) {
            totalLabel.setText(messages.getOrDefault("error.nonNegativeDecimal", "Please enter a non-negative price."));
        }
    }

    private static final class ItemRow {
        private final int index;
        private final Label label = new Label();
        private final TextField priceField = new TextField();
        private final TextField quantityField = new TextField();
        private final HBox node;

        ItemRow(int index, Map<String, String> messages) {
            this.index = index;
            priceField.setPrefWidth(140);
            quantityField.setPrefWidth(80);
            label.setMinWidth(80);
            node = new HBox(8, label, priceField, quantityField);
            updateLabels(messages);
        }

        void updateLabels(Map<String, String> messages) {
            label.setText(String.format(messages.getOrDefault("label.itemNumber", "Item %d"), index));
            priceField.setPromptText(messages.getOrDefault("prompt.price", "Price"));
            quantityField.setPromptText(messages.getOrDefault("prompt.quantity", "Quantity"));
        }

        HBox getNode() {
            return node;
        }

        CartItem toCartItem() {
            BigDecimal price = new BigDecimal(priceField.getText().trim().replace(',', '.'));
            int qty = Integer.parseInt(quantityField.getText().trim());
            return new CartItem(price, qty);
        }
    }
}
