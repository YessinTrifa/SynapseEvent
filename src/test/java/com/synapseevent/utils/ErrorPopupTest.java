package com.synapseevent.utils;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ErrorPopupTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Test different types of error popups
        testErrorPopups();
    }
    
    private void testErrorPopups() {
        // Test validation error
        ErrorPopup.showValidationError("This is a test validation error:\n\n• Field 1 is invalid\n• Field 2 is required");
        
        // Test database error
        ErrorPopup.showDatabaseError("This is a test database error: Connection timeout");
        
        // Test registration error
        ErrorPopup.showRegistrationError("This is a test registration error: Email already exists");
        
        // Test authentication error
        ErrorPopup.showAuthenticationError();
        
        // Test generic error
        ErrorPopup.showError("Test Error", "This is a generic test error message");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
