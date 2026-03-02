package com.synapseevent.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class CreateEventDialogController {

    @FXML private ComboBox<String> eventTypeCombo;

    private Stage stage;
    private String result; // selected type if OK pressed

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getResult() {
        return result;
    }

    @FXML
    private void initialize() {
        // Example types (replace with your real types)
        eventTypeCombo.setItems(FXCollections.observableArrayList(
                "Formation", "Paddle", "Beach", "Meeting", "Conference"
        ));
        eventTypeCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void onClose() {
        result = null;
        stage.close();
    }

    @FXML
    private void onCancel() {
        result = null;
        stage.close();
    }

    @FXML
    private void onOk() {
        result = eventTypeCombo.getValue();
        stage.close();
    }
    public void setEventTypes(List<String> types) {
        eventTypeCombo.setItems(FXCollections.observableArrayList(types));
        if (!types.isEmpty()) eventTypeCombo.getSelectionModel().selectFirst();
    }
}