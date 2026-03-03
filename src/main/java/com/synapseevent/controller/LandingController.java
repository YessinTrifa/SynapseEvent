package com.synapseevent.controller;

import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LandingController {

    @FXML private BorderPane root;   // fx:id="root" in FXML
    @FXML private VBox heroCard;     // fx:id="heroCard" in FXML (right card)

    @FXML
    private void initialize() {
        // Responsive rule: hide right card on small widths (layout reflows)
        root.widthProperty().addListener((obs, oldW, newW) -> {
            boolean compact = newW.doubleValue() < 980;
            if (heroCard != null) {
                heroCard.setVisible(!compact);
                heroCard.setManaged(!compact);
            }
        });
    }

    @FXML
    private void goToLogin() {
        Navigator.get().go("/fxml/login.fxml", "Login - SynapseEvent");
    }

    @FXML
    private void goToRegister() {
        Navigator.get().go("/fxml/register.fxml", "Register - SynapseEvent");
    }
    private void switchScene(String fxmlPath) {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            double w = root.getScene().getWidth();
            double h = root.getScene().getHeight();

            Parent newRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene newScene = new Scene(newRoot, w, h);

            stage.setScene(newScene);
            stage.setTitle("SynapseEvent Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}