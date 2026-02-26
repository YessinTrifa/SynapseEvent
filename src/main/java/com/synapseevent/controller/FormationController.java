package com.synapseevent.controller;

import com.synapseevent.entities.FormationEvent;
import com.synapseevent.entities.Venue;
import com.synapseevent.service.FormationEventService;
import com.synapseevent.service.VenueService;
import com.synapseevent.utils.CurrentUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import com.synapseevent.entities.EventTemplate;
import javafx.scene.control.SpinnerValueFactory;

public class FormationController implements TemplateAware{
    @FXML private TableView<FormationEvent> formationTable;
    @FXML private TableColumn<FormationEvent, Long> idColumn;
    @FXML private TableColumn<FormationEvent, String> nameColumn;
    @FXML private TableColumn<FormationEvent, String> dateColumn;
    @FXML private TableColumn<FormationEvent, String> locationColumn;
    @FXML private TableColumn<FormationEvent, String> capacityColumn;
    @FXML private TableColumn<FormationEvent, String> priceColumn;
    @FXML private TableColumn<FormationEvent, String> statusColumn;

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

    private FormationEventService formationEventService = new FormationEventService();
    private VenueService venueService = new VenueService();

    @FXML
    public void initialize() {
        startTimeHourSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9)
        );
        endTimeHourSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12)
        );
        capacitySpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 20)
        );
        priceSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, 0, 1)
        );
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getLocation() != null ? cellData.getValue().getLocation() : ""));
        capacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCapacity() != null ? cellData.getValue().getCapacity().toString() : ""));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getPrice() != null ? cellData.getValue().getPrice().toString() + " TND" : ""));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

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

        loadData();
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
        try {
            formationTable.setItems(FXCollections.observableArrayList(formationEventService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addFormationEvent() {
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
            FormationEvent event = new FormationEvent(name, date, startTime, endTime,
                location, capacity, price, "admin@synapse.com", description, status);
            try {
                formationEventService.ajouter(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFields();
            loadData();
        }
    }

    @FXML
    private void updateFormationEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setStartTime(LocalTime.of(startTimeHourSpinner.getValue(), 0));
            selected.setEndTime(LocalTime.of(endTimeHourSpinner.getValue(), 0));
            Venue selectedVenue = venueComboBox.getValue();
            selected.setLocation(selectedVenue != null ? selectedVenue.getName() : "");
            selected.setCapacity(capacitySpinner.getValue());
            selected.setPrice(priceSpinner.getValue());
            selected.setDescription(descriptionField.getText());
            if (statusComboBox.getValue() != null) {
                selected.setStatus(statusComboBox.getValue());
            }

            try {
                formationEventService.modifier(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void deleteFormationEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                formationEventService.supprimer(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void publishEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("published");
            try {
                formationEventService.modifier(selected);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAsDraft() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("draft");
            try {
                formationEventService.modifier(selected);
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void selectFormationEvent() {
        FormationEvent selected = formationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        nameField.setText(selected.getName());
        datePicker.setValue(selected.getDate());

        if (selected.getStartTime() != null) {
            startTimeHourSpinner.getValueFactory().setValue(selected.getStartTime().getHour());
        }
        if (selected.getEndTime() != null) {
            endTimeHourSpinner.getValueFactory().setValue(selected.getEndTime().getHour());
        }

        if (selected.getLocation() != null) {
            for (Venue venue : venueComboBox.getItems()) {
                if (venue.getName().equals(selected.getLocation())) {
                    venueComboBox.setValue(venue);
                    break;
                }
            }
        }

        if (selected.getCapacity() != null) {
            capacitySpinner.getValueFactory().setValue(selected.getCapacity());
        }
        if (selected.getPrice() != null) {
            priceSpinner.getValueFactory().setValue(selected.getPrice());
        }

        statusComboBox.setValue(selected.getStatus());
        descriptionField.setText(selected.getDescription());
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(9);
        endTimeHourSpinner.getValueFactory().setValue(17);
        venueComboBox.setValue(null);
        capacitySpinner.getValueFactory().setValue(10);
        priceSpinner.getValueFactory().setValue(0.0);
        descriptionField.clear();
        statusComboBox.setValue("draft");
    }
    @Override
    public void applyTemplate(EventTemplate t) {
        if (t == null) return;

        // Time -> your UI uses only HOUR spinners
        LocalTime st = t.getDefaultStartTime();
        LocalTime et = t.getDefaultEndTime();

        if (st != null && startTimeHourSpinner.getValueFactory() != null) {
            startTimeHourSpinner.getValueFactory().setValue(st.getHour());
        }
        if (et != null && endTimeHourSpinner.getValueFactory() != null) {
            endTimeHourSpinner.getValueFactory().setValue(et.getHour());
        }

        if (t.getDefaultCapacity() != null && capacitySpinner.getValueFactory() != null) {
            capacitySpinner.getValueFactory().setValue(t.getDefaultCapacity());
        }

        if (t.getDefaultPrice() != null && priceSpinner.getValueFactory() != null) {
            priceSpinner.getValueFactory().setValue(t.getDefaultPrice());
        }

        if (t.getDefaultDescription() != null) {
            descriptionField.setText(t.getDefaultDescription());
        }
    }
}
