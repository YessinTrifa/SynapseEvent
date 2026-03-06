package com.synapseevent.controller;

import com.synapseevent.entities.EventInstance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EditEventDialogController {

    @FXML private StackPane overlayRoot;
    @FXML private VBox card;
    @FXML private Label eventTypeLabel;
    
    // Basic Information
    @FXML private TextField nameField;
    @FXML private TextField organizerField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    
    // Schedule & Location
    @FXML private DatePicker datePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField locationField;
    
    // Pricing & Capacity
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Double> priceSpinner;

    private EventInstance event;
    private boolean confirmed = false;

    public void initialize() {
        // Initialize status combo box
        statusComboBox.getItems().addAll("draft", "published", "pending", "confirmed", "cancelled");
        
        // Initialize capacity spinner
        SpinnerValueFactory<Integer> capacityFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50);
        capacitySpinner.setValueFactory(capacityFactory);
        
        // Initialize price spinner
        SpinnerValueFactory<Double> priceFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 10000.0, 0.0, 10.0);
        priceSpinner.setValueFactory(priceFactory);
    }

    public void setEvent(EventInstance event) {
        this.event = event;
        loadEventData();
    }

    private void loadEventData() {
        if (event == null) return;
        
        // Set event type label
        eventTypeLabel.setText("Editing: " + (event.getType() != null ? event.getType() : "Event"));
        
        // Load basic information
        nameField.setText(event.getName());
        organizerField.setText(event.getOrganizer());
        statusComboBox.setValue(event.getStatus());
        descriptionField.setText(event.getDescription());
        
        // Load schedule & location
        datePicker.setValue(event.getDate());
        
        if (event.getStartTime() != null) {
            startTimeField.setText(event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (event.getEndTime() != null) {
            endTimeField.setText(event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        locationField.setText(event.getLocation());
        
        // Load pricing & capacity
        capacitySpinner.getValueFactory().setValue(event.getCapacity() != null ? event.getCapacity() : 50);
        priceSpinner.getValueFactory().setValue(event.getPrice() != null ? event.getPrice() : 0.0);
    }

    @FXML
    private void onClose() {
        closeDialog();
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        closeDialog();
    }

    @FXML
    private void onSave() {
        if (!validateInput()) {
            return;
        }
        
        // Update event object with new values
        event.setName(nameField.getText().trim());
        event.setOrganizer(organizerField.getText().trim());
        event.setStatus(statusComboBox.getValue());
        event.setDescription(descriptionField.getText().trim());
        event.setDate(datePicker.getValue());
        
        // Parse time fields
        try {
            if (startTimeField.getText() != null && !startTimeField.getText().isEmpty()) {
                event.setStartTime(LocalTime.parse(startTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
            }
            if (endTimeField.getText() != null && !endTimeField.getText().isEmpty()) {
                event.setEndTime(LocalTime.parse(endTimeField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
            }
        } catch (Exception e) {
            showAlert("Invalid Time Format", "Please enter time in HH:mm format (e.g., 09:00, 18:30)");
            return;
        }
        
        event.setLocation(locationField.getText().trim());
        event.setCapacity(capacitySpinner.getValue());
        event.setPrice(priceSpinner.getValue());
        
        confirmed = true;
        closeDialog();
    }

    private boolean validateInput() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Event name is required");
            nameField.requestFocus();
            return false;
        }
        
        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Event date is required");
            datePicker.requestFocus();
            return false;
        }
        
        if (locationField.getText() == null || locationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Location is required");
            locationField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) overlayRoot.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public EventInstance getEvent() {
        return event;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
