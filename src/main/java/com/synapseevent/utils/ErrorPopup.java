package com.synapseevent.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ErrorPopup {
    
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        // Create custom content for better formatting
        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 20;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
        
        TextFlow messageFlow = new TextFlow();
        Text messageText = new Text(message);
        messageText.setStyle("-fx-font-size: 14px; -fx-fill: #424242;");
        messageFlow.getChildren().add(messageText);
        
        content.getChildren().addAll(titleLabel, messageFlow);
        alert.getDialogPane().setContent(content);
        
        // Style the dialog
        alert.getDialogPane().setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #d32f2f;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );
        
        alert.showAndWait();
    }
    
    public static void showValidationError(String message) {
        showError("Validation Error", message);
    }
    
    public static void showDatabaseError(String message) {
        showError("Database Error", message);
    }
    
    public static void showRegistrationError(String message) {
        showError("Registration Failed", message);
    }
    
    public static void showAuthenticationError() {
        showError("Authentication Failed", "Invalid email or password. Please check your credentials and try again.");
    }
}
