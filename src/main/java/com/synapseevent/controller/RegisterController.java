package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
import com.synapseevent.utils.MaConnection;
import com.synapseevent.utils.PasswordUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    @FXML
    private Label nomError;
    @FXML
    private Label prenomError;
    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private Label confirmPasswordError;
    @FXML
    private Label phoneError;
    @FXML
    private Label siretError;
    
    // Enterprise fields
    @FXML
    private RadioButton existingEnterpriseRadio;
    @FXML
    private RadioButton newEnterpriseRadio;
    @FXML
    private ComboBox<Entreprise> entrepriseCombo;
    @FXML
    private TextField entrepriseNameField;
    @FXML
    private TextField entrepriseSiretField;

    private UserService userService = new UserService();
    private RoleService roleService = new RoleService();
    private EntrepriseService entrepriseService = new EntrepriseService();

    public RegisterController() {
        // Initialize default data if not exists
        initializeDefaultData();
    }
    
    private void initializeDefaultData() {
        // Create default roles if not exist (use INSERT IGNORE to avoid duplicates)
        Connection conn = MaConnection.getInstance().getConnection();
        try {

            if (conn == null) return;
            
            // Insert roles only if they don't exist
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
            
            // Insert default enterprise only if not exists
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
        // Load existing enterprises into combo box
        try {
            entrepriseCombo.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        nomField.textProperty().addListener((obs, old, newVal) -> clearError(nomError, nomField));
        prenomField.textProperty().addListener((obs, old, newVal) -> clearError(prenomError, prenomField));
        emailField.textProperty().addListener((obs, old, newVal) -> clearError(emailError, emailField));
        passwordField.textProperty().addListener((obs, old, newVal) -> clearError(passwordError, passwordField));
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> clearError(confirmPasswordError, confirmPasswordField));
        phoneField.textProperty().addListener((obs, old, newVal) -> clearError(phoneError, phoneField));
        entrepriseSiretField.textProperty().addListener((obs, old, newVal) -> clearError(siretError, entrepriseSiretField));
    }
    
    @FXML
    private void onExistingEnterpriseSelected() {
        entrepriseCombo.setVisible(true);
        entrepriseNameField.setVisible(false);
        entrepriseSiretField.setVisible(false);
    }
    
    @FXML
    private void onNewEnterpriseSelected() {
        entrepriseCombo.setVisible(false);
        entrepriseNameField.setVisible(true);
        entrepriseSiretField.setVisible(true);
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

    // Nom: letters only, min 2 chars
        if (nom.isEmpty() || nom.length() < 2 || !nom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showError(nomError, nomField, "Last name must be at least 2 letters, no numbers.");
            hasError = true;
        }

    // Prenom: letters only, min 2 chars
        if (prenom.isEmpty() || prenom.length() < 2 || !prenom.matches("[a-zA-ZÀ-ÿ\\s]+")) {
            showError(prenomError, prenomField, "First name must be at least 2 letters, no numbers.");
            hasError = true;
        }

    // Email: proper format
        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError(emailError, emailField, "Please enter a valid email address.");
            hasError = true;
        }

    // Password: min 8 chars, 1 uppercase, 1 digit
        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            showError(passwordError, passwordField, "Min 8 characters, 1 uppercase letter, 1 number.");
            hasError = true;
        }

    // Confirm password
        if (!password.equals(confirmPassword)) {
            showError(confirmPasswordError, confirmPasswordField, "Passwords do not match.");
            hasError = true;
        }

    // Phone: optional but if filled, must be valid
        if (!phone.isEmpty() && !phone.matches("^[+0-9\\s]{8,15}$")) {
            showError(phoneError, phoneField, "Phone must be 8-15 digits.");
            hasError = true;
        }

        if (hasError) return;
        //mail uniqueness
        if (userService.findByEmail(email) != null) {
            showError(emailError, emailField, "This email is already registered.");
            return;
        }

        try {
            // Get User role (default role for new users)
            Role userRole = roleService.getByName("User");
            if (userRole == null) {
                messageLabel.setText("Error: User role not found");
                return;
            }

            // Handle enterprise selection/creation
            Entreprise enterprise;
            if (newEnterpriseRadio.isSelected()) {
                // Create new enterprise
                String entrepriseName = entrepriseNameField.getText().trim();
                String entrepriseSiret = entrepriseSiretField.getText().trim();

                if (entrepriseName.isEmpty() || entrepriseSiret.isEmpty()) {
                    if (entrepriseName.isEmpty()) {
                        messageLabel.setText("Enterprise name is required.");
                        return;
                    }
                    if (!entrepriseSiret.matches("^\\d{14}$")) {
                        showError(siretError, entrepriseSiretField, "SIRET must be exactly 14 digits.");
                        return;
                    }
                    return;

                }
                
                enterprise = new Entreprise();
                enterprise.setNom(entrepriseName);
                enterprise.setSiret(entrepriseSiret);
                entrepriseService.add(enterprise);
            } else {
                // Use selected or default enterprise
                enterprise = entrepriseCombo.getValue();
                if (enterprise == null) {
                    // Try to get default enterprise
                    enterprise = entrepriseService.findbyId(1L);
                }
                if (enterprise == null) {
                    messageLabel.setText("Error: No enterprise selected");
                    return;
                }
            }

            // Store password as plain text (for your use case)
            String storedPassword = password;

            User newUser = new User(
                    email,
                    storedPassword,
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

            // Save user to database
            boolean success = userService.ajouter(newUser);

            if (success) {
                messageLabel.setText("Registration successful! Redirecting to login...");
                messageLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 13; -fx-font-weight: 700;");
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

    //control de saisie errors
    private void showError(Label errorLabel, TextField field, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        field.setStyle(field.getStyle() + "; -fx-border-color: #ff6b6b; -fx-border-width: 2;");
    }

    private void clearError(Label errorLabel, TextField field) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        // Reset to original style
        field.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 12; -fx-border-color: rgba(255,255,255,0.18); -fx-padding: 10 12; -fx-font-size: 12; -fx-border-radius: 12;");
    }
}
