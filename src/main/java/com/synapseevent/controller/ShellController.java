package com.synapseevent.controller;

import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ShellController {

    @FXML private StackPane contentHost;
    @FXML private ScrollPane contentScrollPane;
    @FXML private HBox     navBar;       // the entire top nav bar
    @FXML private Button   backBtn;
    @FXML private Button   forwardBtn;
    @FXML private Label    titleLabel;

    // Pages where the nav bar should NOT appear
    private static final java.util.Set<String> AUTH_PAGES = java.util.Set.of(
            "/fxml/landing.fxml",
            "/fxml/login.fxml",
            "/fxml/register.fxml"
    );

    @FXML
    private void initialize() {
        contentHost.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) return;

            Stage stage = (Stage) newScene.getWindow();

            Navigator.init(stage, contentHost, navBar, titleLabel, backBtn, forwardBtn);

            // Start at landing page — nav bar is hidden for auth pages
            Navigator.get().go("/fxml/landing.fxml");

            // Keyboard shortcuts: Alt+Left = back, Alt+Right = forward, F5 = refresh
            newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN),
                    () -> Navigator.get().back()
            );
            newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN),
                    () -> Navigator.get().forward()
            );
            newScene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.F5),
                    () -> Navigator.get().refresh()
            );
        });
    }

    @FXML private void goBack()    { Navigator.get().back(); }
    @FXML private void goForward() { Navigator.get().forward(); }
    @FXML private void refresh()   { Navigator.get().refresh(); }

    @FXML
    private void logout() {
        CurrentUser.logout();
        // Navigate to login — Navigator will hide the nav bar automatically
        Navigator.get().go("/fxml/login.fxml", "Login - SynapseEvent");
    }
}