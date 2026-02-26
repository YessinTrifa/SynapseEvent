package com.synapseevent.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the landing page.
 * Handles navigation to login and register pages.
 */
public class LandingController {

    /**
     * Navigate to the login page.
     */
    @FXML
    private void goToLogin() {
        loadFXML("/fxml/login.fxml");
    }

    /**
     * Navigate to the register page.
     */
    @FXML
    private void goToRegister() {
        loadFXML("/fxml/register.fxml");
    }

    /**
     * Helper method to load a new FXML scene.
     * @param fxmlPath The path to the FXML file to load
     */
    private void loadFXML(String fxmlPath) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
            stage.setTitle("SynapseEvent Management");
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ": " + e.getMessage());
        }
    }
}
