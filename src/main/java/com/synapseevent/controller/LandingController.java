package com.synapseevent.controller;

import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LandingController {

    @FXML private StackPane rootPane;

    @FXML
    private void initialize() {

    }

    @FXML
    private void goToLogin() {
        Navigator.get().go("/fxml/login.fxml", "Login - SynapseEvent");
    }

    @FXML
    private void goToRegister() {
        Navigator.get().go("/fxml/register.fxml", "Register - SynapseEvent");
    }
 }
