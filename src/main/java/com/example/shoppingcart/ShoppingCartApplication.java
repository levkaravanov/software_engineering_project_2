package com.example.shoppingcart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShoppingCartApplication extends Application {

    public static final String APP_TITLE = "Lev Karavanov / Shopping Cart App";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/shoppingcart/MainView.fxml"));
        Parent root = loader.load();
        stage.setTitle(APP_TITLE);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
