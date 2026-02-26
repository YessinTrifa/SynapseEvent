package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
import com.synapseevent.utils.MaConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    @FXML private ComboBox<Entreprise> entrepriseCombo;

    @FXML private Label messageLabel;
    @FXML private Label nomError;
    @FXML private Label prenomError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;
    @FXML private Label phoneError;

    private final UserService userService = new UserService();
    private final RoleService roleService = new RoleService();
    private final EntrepriseService entrepriseService = new EntrepriseService();

    public RegisterController() {
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        Connection conn = MaConnection.getInstance().getConnection();
        try {
            if (conn == null) return;

            // Roles
            String[] roleNames = {"Admin", "User", "Manager"};
            for (String roleName : roleNames) {
                String checkSql = "SELECT id FROM Role WHERE name = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, roleName);
                    ResultSet rs = checkStmt.executeQuery();
                    if (!rs.next()) {
                        String insertSql = "INSERT INTO Role (name) VALUES (?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, roleName);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }

            // Default enterprise
            String checkEnterpriseSql = "SELECT id FROM Enterprise WHERE nom = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEnterpriseSql)) {
                checkStmt.setString(1, "DefaultCorp");
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    String insertSql = "INSERT INTO Enterprise (nom, siret) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, "DefaultCorp");
                        insertStmt.setString(2, "12345678901234");
                        insertStmt.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Load existing enterprises
        try {
            entrepriseCombo.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear errors live
        nomField.textProperty().addListener((obs, old, v) -> clearError(nomError, nomField));
        prenomField.textProperty().addListener((obs, old, v) -> clearError(prenomError, prenomField));
        emailField.textProperty().addListener((obs, old, v) -> clearError(emailError, emailField));
        passwordField.textProperty().addListener((obs, old, v) -> clearError(passwordError, passwordField));
        confirmPasswordField.textProperty().addListener((obs, old, v) -> clearError(confirmPasswordError, confirmPasswordField));
        phoneField.textProperty().addListener((obs, old, v) -> clearError(phoneError, phoneField));
    }

    @FXML
    private void registerUser() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        boolean hasError = false;

        if (nom.isEmpty() || nom.length() < 2 || !nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showError(nomError, nomField, "Last name must be at least 2 letters, no numbers.");
            hasError = true;
        }

        if (prenom.isEmpty() || prenom.length() < 2 || !prenom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showError(prenomError, prenomField, "First name must be at least 2 letters, no numbers.");
            hasError = true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError(emailError, emailField, "Please enter a valid email address.");
            hasError = true;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            showError(passwordError, passwordField, "Min 8 characters, 1 uppercase letter, 1 number.");
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            showError(confirmPasswordError, confirmPasswordField, "Passwords do not match.");
            hasError = true;
        }

        if (!phone.isEmpty() && !phone.matches("^[+0-9\\s]{8,15}$")) {
            showError(phoneError, phoneField, "Phone must be 8-15 digits.");
            hasError = true;
        }

        if (hasError) return;

        if (userService.findByEmail(email) != null) {
            showError(emailError, emailField, "This email is already registered.");
            return;
        }

        try {
            Role userRole = roleService.getByName("User");
            if (userRole == null) {
                messageLabel.setText("Error: User role not found");
                return;
            }

            // ✅ ONLY select existing enterprise (no type, no create-new)
            Entreprise enterprise = entrepriseCombo.getValue();
            if (enterprise == null) {
                messageLabel.setText("Please select an enterprise.");
                messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
                return;
            }

            User newUser = new User(
                    email,
                    password,
                    nom,
                    prenom,
                    phone,
                    address,
                    null,
                    userRole.getId(),
                    enterprise.getId()
            );

            newUser.setRole(userRole);
            newUser.setEnterprise(enterprise);

            boolean success = userService.ajouter(newUser);

            if (success) {
                messageLabel.setText("Registration successful! Redirecting to login...");
                messageLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 13; -fx-font-weight: 700;");

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
                messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
            }

        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
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

    private void showError(Label errorLabel, TextField field, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        field.setStyle(field.getStyle() + "; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
    }

    private void clearError(Label errorLabel, TextField field) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        // ✅ IMPORTANT: do NOT force white backgrounds — let CSS handle it
        field.setStyle("");
    }
}