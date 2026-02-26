package com.synapseevent.controller;

import com.synapseevent.entities.PaddleEvent;
import com.synapseevent.entities.Venue;
import com.synapseevent.service.PaddleEventService;
import com.synapseevent.service.VenueService;
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

public class PaddleController implements TemplateAware {
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
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Integer> reservationSpinner;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField mapField;
    @FXML private CheckBox disponibiliteCheckBox;

    private PaddleEventService paddleEventService = new PaddleEventService();
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
        reservationSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0)
        );
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
        
        // Add table selection listener to load event data when selected
        paddleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectPaddleEvent();
            }
        });

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
        Venue selectedVenue = venueComboBox.getValue();
        String location = selectedVenue != null ? selectedVenue.getName() : "";
        String map = mapField.getText();
        Integer capacity = capacitySpinner.getValue();
        Integer reservation = reservationSpinner.getValue();
        Double price = priceSpinner.getValue();
        Boolean disponibilite = disponibiliteCheckBox.isSelected();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";

        if (name != null && !name.isEmpty() && date != null) {
            PaddleEvent event = new PaddleEvent(name, date, startTime, endTime,
                location, map, capacity, reservation, price, disponibilite, "admin@synapse.com", description, status);
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
            Venue selectedVenue = venueComboBox.getValue();
            selected.setLocation(selectedVenue != null ? selectedVenue.getName() : "");
            selected.setMap(mapField.getText());
            selected.setCapacity(capacitySpinner.getValue());
            selected.setReservation(reservationSpinner.getValue());
            selected.setPrice(priceSpinner.getValue());
            selected.setDisponibilite(disponibiliteCheckBox.isSelected());
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

            // Try to find and select the venue in the combo box
            if (selected.getLocation() != null) {
                for (Venue venue : venueComboBox.getItems()) {
                    if (venue.getName().equals(selected.getLocation())) {
                        venueComboBox.setValue(venue);
                        break;
                    }
                }
            }
            
            // Set map field
            if (selected.getMap() != null) {
                mapField.setText(selected.getMap());
            } else {
                mapField.clear();
            }
            
            // Set capacity and reservation
            if (selected.getCapacity() != null) {
                capacitySpinner.getValueFactory().setValue(selected.getCapacity());
            }
            if (selected.getReservation() != null) {
                reservationSpinner.getValueFactory().setValue(selected.getReservation());
            }
            
            // Set price
            if (selected.getPrice() != null) {
                priceSpinner.getValueFactory().setValue(selected.getPrice());
            }
            
            // Set disponibilite
            if (selected.getDisponibilite() != null) {
                disponibiliteCheckBox.setSelected(selected.getDisponibilite());
            } else {
                disponibiliteCheckBox.setSelected(true);
            }
            
            // Set status and description
            statusComboBox.setValue(selected.getStatus());
            descriptionField.setText(selected.getDescription());
        }
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(9);
        endTimeHourSpinner.getValueFactory().setValue(17);
        venueComboBox.setValue(null);
        venueTypeFilterComboBox.setValue("All");
        mapField.clear();
        capacitySpinner.getValueFactory().setValue(20);
        reservationSpinner.getValueFactory().setValue(0);
        priceSpinner.getValueFactory().setValue(0.0);
        statusComboBox.setValue("draft");
        descriptionField.clear();
        disponibiliteCheckBox.setSelected(true);
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
