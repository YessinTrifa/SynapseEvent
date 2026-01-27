package com.synapseevent.controller;

import com.synapseevent.entities.User;
import com.synapseevent.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserService userService = new UserService();

    @FXML
    private void loginAsAdmin() {
        String emailText = emailField.getText();
        String passwordText = passwordField.getText();
        User user = userService.authenticate(emailText, passwordText);
        if (user != null && "Admin".equals(user.getRole().getName())) {
            loadFXML("/fxml/adminDashboard.fxml");
        } else {
            // Show error
            System.out.println("Access denied: Invalid credentials or not an admin");
        }
    }

    @FXML
    private void loginAsUser() {
        String emailText = emailField.getText();
        String passwordText = passwordField.getText();
        User user = userService.authenticate(emailText, passwordText);
        if (user != null) {
            loadFXML("/fxml/userDashboard.fxml");
        } else {
            System.out.println("Invalid credentials");
        }
    }

    private void loadFXML(String fxmlPath) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}