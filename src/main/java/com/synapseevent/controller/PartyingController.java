package com.synapseevent.controller;

import com.synapseevent.entities.PartyingEvent;
import com.synapseevent.service.PartyingEventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class PartyingController {
    @FXML private TableView<PartyingEvent> partyingTable;
    @FXML private TableColumn<PartyingEvent, Long> idColumn;
    @FXML private TableColumn<PartyingEvent, String> nameColumn;
    @FXML private TableColumn<PartyingEvent, String> dateColumn;
    @FXML private TableColumn<PartyingEvent, String> descriptionColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;

    private PartyingEventService partyingEventService = new PartyingEventService();

    @FXML public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();
    }

    private void loadData() {
        try {
            partyingTable.setItems(FXCollections.observableArrayList(partyingEventService.readAll()));
        } catch (Exception e) {
            // Handle exception
        }
    }

    @FXML private void addPartyingEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText();
        if (name != null && !name.isEmpty() && date != null && description != null && !description.isEmpty()) {
            PartyingEvent event = new PartyingEvent(name, date, description);
            try {
                partyingEventService.ajouter(event);
            } catch (Exception e) {
                // Handle exception
            }
            clearFields();
            loadData();
        }
    }

    @FXML private void updatePartyingEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setDescription(descriptionField.getText());
            try {
                partyingEventService.modifier(selected);
            } catch (Exception e) {
                // Handle exception
            }
            loadData();
        }
    }

    @FXML private void deletePartyingEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                partyingEventService.supprimer(selected);
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