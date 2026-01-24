package com.synapseevent.controller;

import com.synapseevent.entities.FormationEvent;
import com.synapseevent.service.FormationEventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class FormationController {
    @FXML private TableView<FormationEvent> formationTable;
    @FXML private TableColumn<FormationEvent, Long> idColumn;
    @FXML private TableColumn<FormationEvent, String> nameColumn;
    @FXML private TableColumn<FormationEvent, String> dateColumn;
    @FXML private TableColumn<FormationEvent, String> descriptionColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;

    private FormationEventService formationEventService = new FormationEventService();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();
    }

    private void loadData() {
        try {
            formationTable.setItems(FXCollections.observableArrayList(formationEventService.readAll()));
        } catch (Exception e) {
            // Handle exception
        }
    }

    @FXML private void addFormationEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText();
        if (name != null && !name.isEmpty() && date != null && description != null && !description.isEmpty()) {
            FormationEvent event = new FormationEvent(name, date, description);
            try {
                formationEventService.ajouter(event);
            } catch (Exception e) {
                // Handle exception
            }
            clearFields();
            loadData();
        }
    }

    @FXML private void updateFormationEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setDescription(descriptionField.getText());
            try {
                formationEventService.modifier(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    @FXML private void deleteFormationEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                formationEventService.supprimer(selected);
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