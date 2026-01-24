package com.synapseevent.controller;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.service.PaddleEventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class PaddleController {
    @FXML private TableView<PaddleEvent> paddleTable;
    @FXML private TableColumn<PaddleEvent, Long> idColumn;
    @FXML private TableColumn<PaddleEvent, String> nameColumn;
    @FXML private TableColumn<PaddleEvent, String> dateColumn;
    @FXML private TableColumn<PaddleEvent, String> descriptionColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;

    private PaddleEventService paddleEventService = new PaddleEventService();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();
    }

    private void loadData() {
        try {
            paddleTable.setItems(FXCollections.observableArrayList(paddleEventService.readAll()));
        } catch (Exception e) {
            // Handle exception
        }
    }

    @FXML private void addPaddleEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText();
        if (name != null && !name.isEmpty() && date != null && description != null && !description.isEmpty()) {
            PaddleEvent event = new PaddleEvent(name, date, description);
            try {
                paddleEventService.ajouter(event);
            } catch (Exception e) {
                // Handle exception
            }
            clearFields();
            loadData();
        }
    }

    @FXML private void updatePaddleEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setDescription(descriptionField.getText());
            try {
                paddleEventService.modifier(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    @FXML private void deletePaddleEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                paddleEventService.supprimer(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        descriptionField.clear();
    }
}