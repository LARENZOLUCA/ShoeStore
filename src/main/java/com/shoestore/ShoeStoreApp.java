package com.shoestore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ShoeStoreApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Устанавливаем иконку приложения
        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }

        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        Parent root = FXMLLoader.load(ShoeStoreApp.class.getResource("/fxml/login.fxml"));
        primaryStage.setTitle("Обувь - Вход в систему");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showProductListScreen() throws Exception {
        Parent root = FXMLLoader.load(ShoeStoreApp.class.getResource("/fxml/product_list.fxml"));
        primaryStage.setTitle("Обувь - Список товаров");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}