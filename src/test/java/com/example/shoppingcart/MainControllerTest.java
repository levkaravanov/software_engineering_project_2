package com.example.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@EnabledOnOs(OS.LINUX)
class MainControllerTest {

    private static final Map<String, String> ENGLISH_MESSAGES = Map.of(
            "ui.selectLanguage", "Select language:",
            "prompt.itemCount", "Number of items:",
            "button.enterItems", "Enter items",
            "button.calculate", "Calculate total",
            "message.totalCost", "Total cost:",
            "error.nonNegativeInteger", "Please enter a non-negative whole number.",
            "error.nonNegativeDecimal", "Please enter a non-negative price.",
            "label.itemNumber", "Item %d",
            "prompt.price", "Price",
            "prompt.quantity", "Quantity"
    );

    private static final Map<String, String> ARABIC_MESSAGES = Map.of(
            "ui.selectLanguage", "اختر اللغة:",
            "prompt.itemCount", "أدخل عدد العناصر:",
            "button.enterItems", "إدخال العناصر",
            "button.calculate", "احسب الإجمالي",
            "message.totalCost", "التكلفة الإجمالية:",
            "error.nonNegativeInteger", "الرجاء إدخال عدد صحيح غير سالب.",
            "error.nonNegativeDecimal", "الرجاء إدخال سعر غير سالب.",
            "label.itemNumber", "العنصر %d",
            "prompt.price", "السعر",
            "prompt.quantity", "الكمية"
    );

    @Mock
    private LocalizationService localizationService;

    @Mock
    private CartService cartService;

    private MainController controller;
    private Label languageLabel;
    private ChoiceBox<String> languageChoice;
    private Label itemCountLabel;
    private TextField itemCountField;
    private Button generateButton;
    private VBox itemsContainer;
    private Button calculateButton;
    private Label totalLabel;
    private VBox root;

    @BeforeAll
    static void initJavaFx() {
        FxTestUtils.initToolkit();
    }

    @BeforeEach
    void setUp() {
        when(localizationService.getStrings(anyString())).thenAnswer(invocation -> {
            String language = invocation.getArgument(0, String.class);
            return "ar".equals(language) ? ARABIC_MESSAGES : ENGLISH_MESSAGES;
        });

        controller = new MainController(new CartCalculator(), localizationService, cartService);

        FxTestUtils.runOnFxThread(() -> {
            languageLabel = new Label();
            languageChoice = new ChoiceBox<>();
            itemCountLabel = new Label();
            itemCountField = new TextField();
            generateButton = new Button();
            itemsContainer = new VBox();
            calculateButton = new Button();
            totalLabel = new Label();
            root = new VBox();
            controller.bindView(
                    languageLabel,
                    languageChoice,
                    itemCountLabel,
                    itemCountField,
                    generateButton,
                    itemsContainer,
                    calculateButton,
                    totalLabel,
                    root
            );
            controller.initialize();
        });
    }

    @Test
    void initializeLoadsEnglishLabelsByDefault() {
        assertEquals("Select language:", languageLabel.getText());
        assertEquals("Number of items:", itemCountLabel.getText());
        assertEquals("Enter items", generateButton.getText());
        assertEquals("Calculate total", calculateButton.getText());
        assertEquals("Total cost: ", totalLabel.getText());
        assertEquals(NodeOrientation.LEFT_TO_RIGHT, root.getNodeOrientation());
        verify(localizationService).getStrings("en");
    }

    @Test
    void switchingToArabicUpdatesOrientationAndExistingRows() {
        FxTestUtils.runOnFxThread(() -> {
            itemCountField.setText("1");
            controller.onGenerateItems();
            languageChoice.getSelectionModel().select(4);
        });

        HBox row = rowAt(0);
        Label rowLabel = (Label) row.getChildren().get(0);
        TextField priceField = (TextField) row.getChildren().get(1);
        TextField quantityField = (TextField) row.getChildren().get(2);

        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        assertEquals("اختر اللغة:", languageLabel.getText());
        assertEquals("العنصر 1", rowLabel.getText());
        assertEquals("السعر", priceField.getPromptText());
        assertEquals("الكمية", quantityField.getPromptText());
        verify(localizationService, atLeastOnce()).getStrings("ar");
    }

    @Test
    void invalidItemCountShowsValidationError() {
        FxTestUtils.runOnFxThread(() -> {
            itemCountField.setText("-1");
            controller.onGenerateItems();
        });

        assertEquals("Please enter a non-negative whole number.", totalLabel.getText());
        assertTrue(itemsContainer.getChildren().isEmpty());
    }

    @Test
    void validItemCountCreatesRequestedRows() {
        FxTestUtils.runOnFxThread(() -> {
            itemCountField.setText("2");
            controller.onGenerateItems();
        });

        assertEquals(2, itemsContainer.getChildren().size());
        HBox firstRow = rowAt(0);
        Label firstLabel = (Label) firstRow.getChildren().get(0);
        TextField firstPrice = (TextField) firstRow.getChildren().get(1);
        TextField firstQuantity = (TextField) firstRow.getChildren().get(2);

        assertEquals("Item 1", firstLabel.getText());
        assertEquals("Price", firstPrice.getPromptText());
        assertEquals("Quantity", firstQuantity.getPromptText());
        assertEquals("Total cost: ", totalLabel.getText());
    }

    @Test
    void validCalculateFormatsTotalAndSavesCart() {
        FxTestUtils.runOnFxThread(() -> {
            itemCountField.setText("2");
            controller.onGenerateItems();
            setRowValues(0, "3.50", "2");
            setRowValues(1, "1.25", "4");
            controller.onCalculate();
        });

        ArgumentCaptor<List<CartItem>> itemsCaptor = ArgumentCaptor.forClass(List.class);

        assertEquals("Total cost: 12.00", totalLabel.getText());
        verify(cartService).saveCart(eq(2), eq(12.0), eq("en"), itemsCaptor.capture());
        assertEquals(
                List.of(
                        new CartItem(new java.math.BigDecimal("3.50"), 2),
                        new CartItem(new java.math.BigDecimal("1.25"), 4)
                ),
                itemsCaptor.getValue()
        );
    }

    @Test
    void invalidItemInputShowsValidationError() {
        FxTestUtils.runOnFxThread(() -> {
            itemCountField.setText("1");
            controller.onGenerateItems();
            setRowValues(0, "-1.00", "1");
            controller.onCalculate();
        });

        assertEquals("Please enter a non-negative price.", totalLabel.getText());
    }

    private HBox rowAt(int index) {
        return FxTestUtils.callOnFxThread(() -> (HBox) itemsContainer.getChildren().get(index));
    }

    private void setRowValues(int rowIndex, String price, String quantity) {
        HBox row = (HBox) itemsContainer.getChildren().get(rowIndex);
        ((TextField) row.getChildren().get(1)).setText(price);
        ((TextField) row.getChildren().get(2)).setText(quantity);
    }
}
