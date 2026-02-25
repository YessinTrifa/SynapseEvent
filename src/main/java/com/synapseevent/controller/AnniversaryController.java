package com.synapseevent.controller;

import com.synapseevent.entities.AnniversaryEvent;
import com.synapseevent.entities.Venue;
import com.synapseevent.service.AnniversaryEventService;
import com.synapseevent.service.VenueService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class AnniversaryController {
    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> startTimeHourSpinner;
    @FXML private Spinner<Integer> endTimeHourSpinner;
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;

    private AnniversaryEventService anniversaryEventService = new AnniversaryEventService();
    private VenueService venueService = new VenueService();

    @FXML
    public void initialize() {
        // Set status ComboBox items programmatically
        ObservableList<String> statuses = FXCollections.observableArrayList("draft", "published", "cancelled");
        statusComboBox.setItems(statuses);
        statusComboBox.setValue("draft");

        // Set venue type filter items
        ObservableList<String> venueTypes = FXCollections.observableArrayList("All", "CLUB", "BEACH", "HOTEL", "RESTAURANT");
        venueTypeFilterComboBox.setItems(venueTypes);
        venueTypeFilterComboBox.setValue("All");

        // Load venues and set up filter listener
        loadVenues();
        venueTypeFilterComboBox.setOnAction(e -> filterVenuesByType());
    }

    private void loadVenues() {
        try {
            venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterVenuesByType() {
        String selectedType = venueTypeFilterComboBox.getValue();
        try {
            if ("All".equals(selectedType)) {
                venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
            } else {
                venueComboBox.setItems(FXCollections.observableArrayList(venueService.findByType(selectedType)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        // No table to load data into in anniversary.fxml
    }

    @FXML
    private void addAnniversaryEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), 0);
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), 0);
        Venue selectedVenue = venueComboBox.getValue();
        String location = selectedVenue != null ? selectedVenue.getName() : "";
        Integer capacity = capacitySpinner.getValue();
        Double price = priceSpinner.getValue();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";

        if (name != null && !name.isEmpty() && date != null) {
            AnniversaryEvent event = new AnniversaryEvent(name, date, startTime, endTime,
                location, capacity, price, "admin@synapse.com", null, description, status);
            try {
                anniversaryEventService.ajouter(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFields();
            loadData();
        }
    }

    @FXML
    private void updateAnniversaryEvent() {
        // No table to get selected item from in anniversary.fxml
    }

    @FXML
    private void deleteAnniversaryEvent() {
        // No table to get selected item from in anniversary.fxml
    }

    @FXML
    private void publishEvent() {
        // No table to get selected item from in anniversary.fxml
    }

    @FXML
    private void saveAsDraft() {
        // No table to get selected item from in anniversary.fxml
    }

    @FXML
    private void selectAnniversaryEvent() {
        // No table to get selected item from in anniversary.fxml
    }

    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(14);
        endTimeHourSpinner.getValueFactory().setValue(22);
        venueComboBox.setValue(null);
        venueTypeFilterComboBox.setValue("All");
        capacitySpinner.getValueFactory().setValue(40);
        priceSpinner.getValueFactory().setValue(0.0);
        statusComboBox.setValue("draft");
        descriptionField.clear();
    }
}
