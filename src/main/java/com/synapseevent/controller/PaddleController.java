package com.synapseevent.controller;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.service.PaddleEventService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaddleController {
    @FXML private TableView<PaddleEvent> paddleTable;
    @FXML private TableColumn<PaddleEvent, Long> idColumn;
    @FXML private TableColumn<PaddleEvent, String> nameColumn;
    @FXML private TableColumn<PaddleEvent, String> dateColumn;
    @FXML private TableColumn<PaddleEvent, String> descriptionColumn;
    @FXML private TableColumn<PaddleEvent, String> locationColumn;
    @FXML private TableColumn<PaddleEvent, String> capacityColumn;
    @FXML private TableColumn<PaddleEvent, String> priceColumn;
    @FXML private TableColumn<PaddleEvent, String> statusColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> startTimeHourSpinner;
    @FXML private Spinner<Integer> endTimeHourSpinner;
    @FXML private TextField locationField;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;

    private PaddleEventService paddleEventService = new PaddleEventService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getLocation() != null ? cellData.getValue().getLocation() : ""));
        capacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCapacity() != null ? cellData.getValue().getCapacity().toString() : ""));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getPrice() != null ? cellData.getValue().getPrice().toString() + "€" : ""));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set status ComboBox items programmatically
        ObservableList<String> statuses = FXCollections.observableArrayList("draft", "published", "cancelled");
        statusComboBox.setItems(statuses);
        statusComboBox.setValue("draft");

        loadData();
    }

    private void loadData() {
        try {
            paddleTable.setItems(FXCollections.observableArrayList(paddleEventService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addPaddleEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), 0);
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), 0);
        String location = locationField.getText();
        Integer capacity = capacitySpinner.getValue();
        Double price = priceSpinner.getValue();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";

        if (name != null && !name.isEmpty() && date != null) {
            PaddleEvent event = new PaddleEvent(name, date, startTime, endTime,
                location, capacity, price, "admin@synapse.com", description, status);
            try {
                paddleEventService.ajouter(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFields();
            loadData();
        }
    }

    @FXML
    private void updatePaddleEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setStartTime(LocalTime.of(startTimeHourSpinner.getValue(), 0));
            selected.setEndTime(LocalTime.of(endTimeHourSpinner.getValue(), 0));
            selected.setLocation(locationField.getText());
            selected.setCapacity(capacitySpinner.getValue());
            selected.setPrice(priceSpinner.getValue());
            selected.setDescription(descriptionField.getText());
            if (statusComboBox.getValue() != null) {
                selected.setStatus(statusComboBox.getValue());
            }

            try {
                paddleEventService.modifier(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void deletePaddleEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                paddleEventService.supprimer(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void publishEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("published");
            try {
                paddleEventService.modifier(selected);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAsDraft() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("draft");
            try {
                paddleEventService.modifier(selected);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void selectPaddleEvent() {
        PaddleEvent selected = paddleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            datePicker.setValue(selected.getDate());

            if (selected.getStartTime() != null) {
                startTimeHourSpinner.getValueFactory().setValue(selected.getStartTime().getHour());
            }
            if (selected.getEndTime() != null) {
                endTimeHourSpinner.getValueFactory().setValue(selected.getEndTime().getHour());
            }

            locationField.setText(selected.getLocation());
            if (selected.getCapacity() != null) {
                capacitySpinner.getValueFactory().setValue(selected.getCapacity());
            }
            if (selected.getPrice() != null) {
                priceSpinner.getValueFactory().setValue(selected.getPrice());
            }
            statusComboBox.setValue(selected.getStatus());
            descriptionField.setText(selected.getDescription());
        }
    }

    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(9);
        endTimeHourSpinner.getValueFactory().setValue(17);
        locationField.clear();
        capacitySpinner.getValueFactory().setValue(20);
        priceSpinner.getValueFactory().setValue(0.0);
        statusComboBox.setValue("draft");
        descriptionField.clear();
    }
}
