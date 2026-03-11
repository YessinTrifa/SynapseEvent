package com.synapseevent.controller;

import com.synapseevent.entities.User;
import com.synapseevent.service.UserService;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.Navigator;
import com.synapseevent.utils.ScrollingPlaceholder;
import com.synapseevent.utils.ErrorPopup;
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
    public void initialize() {
        // Clear errors when user starts typing
        emailField.textProperty().addListener((obs, old, v) -> {
            if (!v.isEmpty()) {
                clearFieldErrors();
            }
        });
        
        passwordField.textProperty().addListener((obs, old, v) -> {
            if (!v.isEmpty()) {
                clearFieldErrors();
            }
        });
    }

    @FXML
    private void login() {
        String emailText = emailField.getText();
        String passwordText = passwordField.getText();

        // Reset field styles and placeholders
        clearFieldErrors();

        // Basic validation
        if (emailText == null || emailText.trim().isEmpty() || 
            passwordText == null || passwordText.trim().isEmpty()) {
            setAuthenticationError();
            return;
        }

        User user = userService.authenticate(emailText, passwordText);

        if (user == null) {
            setAuthenticationError();
            return;
        }

        CurrentUser.setCurrentUser(user);

        String roleName = (user.getRole() != null) ? user.getRole().getName() : "";

        if ("Admin".equalsIgnoreCase(roleName)) {
            Navigator.get().go("/fxml/adminDashboard.fxml", "Admin Dashboard - SynapseEvent");
        } else {
            Navigator.get().go("/fxml/userDashboard.fxml", "User Dashboard - SynapseEvent");
        }
    }

    @FXML
    private void goToRegister() {
        Navigator.get().go("/fxml/register.fxml", "Register - SynapseEvent");
    }

    private void loadFXML(String fxmlPath) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(1280);
            stage.setHeight(800);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAuthenticationError() {
        String errorMessage = "Invalid email or password. Please check your credentials and try again.";
        
        // Clear both fields and set error message for security
        emailField.clear();
        emailField.setPromptText("Invalid email or password");
        emailField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
        
        passwordField.clear();
        passwordField.setPromptText("Invalid email or password. Please check your credentials and try again.");
        passwordField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
    }

    private void clearFieldErrors() {
        ScrollingPlaceholder.stopScrolling(emailField);
        emailField.setPromptText("email");
        emailField.setStyle("");
        
        ScrollingPlaceholder.stopScrolling(passwordField);
        passwordField.setPromptText("...............");
        passwordField.setStyle("");
    }
}