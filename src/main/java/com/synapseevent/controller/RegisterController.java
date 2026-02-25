package com.synapseevent.controller;

import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.User;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.UserService;
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
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/synapse_event", "root", "");
            
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
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
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

            // Handle enterprise selection/creation
            Entreprise enterprise;
            if (newEnterpriseRadio.isSelected()) {
                // Create new enterprise
                String entrepriseName = entrepriseNameField.getText().trim();
                String entrepriseSiret = entrepriseSiretField.getText().trim();
                
                if (entrepriseName.isEmpty() || entrepriseSiret.isEmpty()) {
                    messageLabel.setText("Please fill in enterprise name and SIRET");
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
