package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
import com.synapseevent.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private Label messageLabel;

    private UserService userService = new UserService();
    private RoleService roleService = new RoleService();
    private EntrepriseService entrepriseService = new EntrepriseService();

    @FXML
    private void registerUser() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Validate required fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all required fields");
            return;
        }

        // Validate password match
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            return;
        }

        // Validate email format
        if (!email.contains("@") || !email.contains(".")) {
            messageLabel.setText("Please enter a valid email address");
            return;
        }

        // Check if email already exists
        if (userService.findByEmail(email) != null) {
            messageLabel.setText("Email already registered");
            return;
        }

        try {
            // Get User role (default role for new users)
            Role userRole = roleService.getByName("User");
            if (userRole == null) {
                messageLabel.setText("Error: User role not found");
                return;
            }

            // Get default enterprise (use first enterprise as default)
            Entreprise defaultEnterprise = entrepriseService.findbyId(1L);
            if (defaultEnterprise == null) {
                messageLabel.setText("Error: Default enterprise not found");
                return;
            }

            // Hash the password
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Create new user
            User newUser = new User(email, hashedPassword, nom, prenom, phone, address, null, userRole, defaultEnterprise);

            // Save user to database
            boolean success = userService.ajouter(newUser);

            if (success) {
                messageLabel.setText("Registration successful! Please login.");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Optionally redirect to login after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(this::goToLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Error during registration");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) nomField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
