package com.example.shoppingcart;

import java.net.URL;
import java.util.Arrays;

public class Launcher {

    private static final String SMOKE_TEST_FLAG = "--smoke-test";

    public static void main(String[] args) {
        if (Arrays.asList(args).contains(SMOKE_TEST_FLAG)) {
            runSmokeTest();
            return;
        }
        launchJavaFxApplication(args);
    }

    private static void runSmokeTest() {
        URL mainView = Launcher.class.getResource("/com/example/shoppingcart/MainView.fxml");
        if (mainView == null) {
            throw new IllegalStateException("Smoke test failed: MainView.fxml is missing from the packaged jar");
        }

        System.out.println("shopping-cart smoke test passed");
    }

    private static void launchJavaFxApplication(String[] args) {
        try {
            Class<?> applicationClass = Class.forName("com.example.shoppingcart.ShoppingCartApplication");
            applicationClass.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to start the JavaFX application", e);
        }
    }
}
