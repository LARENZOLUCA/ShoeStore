package com.shoestore.controller;

import com.shoestore.ShoeStoreApp;
import com.shoestore.dao.UserDAO;
import com.shoestore.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Введите логин и пароль");
            return;
        }

        User user = userDAO.findByLogin(login);
        if (user == null) {
            errorLabel.setText("Пользователь не найден");
            return;
        }

        if (userDAO.checkPassword(login, password)) {
            try {
                UserSession.getInstance().setCurrentUser(user);
                ShoeStoreApp.showProductListScreen();
            } catch (Exception e) {
                errorLabel.setText("Ошибка загрузки: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Неверный пароль");
        }
    }

    @FXML
    private void handleGuestLogin() {
        try {
            UserSession.getInstance().setCurrentUser(null); // Гость
            ShoeStoreApp.showProductListScreen();
        } catch (Exception e) {
            errorLabel.setText("Ошибка загрузки: " + e.getMessage());
        }
    }
}