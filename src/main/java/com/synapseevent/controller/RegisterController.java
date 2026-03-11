package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
import com.synapseevent.utils.MaConnection;
import com.synapseevent.utils.Navigator;
import com.synapseevent.utils.ScrollingPlaceholder;
import com.synapseevent.utils.ErrorPopup;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

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

        // Clear errors and placeholders when user starts typing
        nomField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(nomField, "Last name");
        });
        prenomField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(prenomField, "Name");
        });
        emailField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(emailField, "email");
        });
        passwordField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(passwordField, "............");
        });
        confirmPasswordField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(confirmPasswordField, "............");
        });
        phoneField.textProperty().addListener((obs, old, v) -> {
            clearFieldError(phoneField, "phone");
        });
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
            nomField.clear();
            nomField.setPromptText("Last name must be at least 2 letters, no numbers.");
            nomField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (prenom.isEmpty() || prenom.length() < 2 || !prenom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            prenomField.clear();
            prenomField.setPromptText("First name must be at least 2 letters, no numbers.");
            prenomField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            emailField.clear();
            emailField.setPromptText("Please enter a valid email address.");
            emailField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            passwordField.clear();
            passwordField.setPromptText("Password must be at least 8 characters with 1 uppercase letter and 1 number.");
            passwordField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.clear();
            confirmPasswordField.setPromptText("Passwords do not match.");
            confirmPasswordField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (!phone.isEmpty() && !phone.matches("^[+0-9\\s]{8,15}$")) {
            phoneField.clear();
            phoneField.setPromptText("Phone number must be 8-15 digits long.");
            phoneField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        if (userService.findByEmail(email) != null) {
            emailField.clear();
            emailField.setPromptText("This email address is already registered.");
            emailField.setStyle("-fx-prompt-text-fill: #ff6b6b; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
            return;
        }

        try {
            Role userRole = roleService.getByName("User");
            if (userRole == null) {
                messageLabel.setText("User role not found in the database. Please contact the system administrator.");
                messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
                return;
            }

            // ✅ ONLY select existing enterprise (no type, no create-new)
            Entreprise enterprise = entrepriseCombo.getValue();
            if (enterprise == null) {
                messageLabel.setText("Please select an enterprise from the dropdown list.");
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
                messageLabel.setText("Failed to create your account. Please try again later or contact support if the problem persists.");
                messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
            }

        } catch (Exception e) {
            messageLabel.setText("An error occurred during registration: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 13; -fx-font-weight: 700;");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Navigator.get().go("/fxml/login.fxml", "Login - SynapseEvent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFieldError(TextField field, String originalPlaceholder) {
        ScrollingPlaceholder.stopScrolling(field);
        field.setPromptText(originalPlaceholder);
        field.setStyle("");
    }
}