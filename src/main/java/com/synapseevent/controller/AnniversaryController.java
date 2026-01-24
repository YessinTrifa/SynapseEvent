package com.synapseevent.controller;

import com.synapseevent.entities.AnniversaryEvent;
import com.synapseevent.service.AnniversaryEventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class AnniversaryController {
    @FXML private TableView<AnniversaryEvent> anniversaryTable;
    @FXML private TableColumn<AnniversaryEvent, Long> idColumn;
    @FXML private TableColumn<AnniversaryEvent, String> nameColumn;
    @FXML private TableColumn<AnniversaryEvent, String> dateColumn;
    @FXML private TableColumn<AnniversaryEvent, String> descriptionColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;

    private AnniversaryEventService anniversaryEventService = new AnniversaryEventService();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();
    }

    private void loadData() {
        try {
            anniversaryTable.setItems(FXCollections.observableArrayList(anniversaryEventService.readAll()));
        } catch (Exception e) {
            // Handle exception
        }
    }

    @FXML private void addAnniversaryEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText();
        if (name != null && !name.isEmpty() && date != null && description != null && !description.isEmpty()) {
            AnniversaryEvent event = new AnniversaryEvent(name, date, description);
            try {
                anniversaryEventService.ajouter(event);
            } catch (Exception e) {
                // Handle exception
            }
            clearFields();
            loadData();
        }
    }

    @FXML private void updateAnniversaryEvent() {
        AnniversaryEvent selected = anniversaryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setDescription(descriptionField.getText());
            try {
                anniversaryEventService.modifier(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    @FXML private void deleteAnniversaryEvent() {
        AnniversaryEvent selected = anniversaryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                anniversaryEventService.supprimer(selected);
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