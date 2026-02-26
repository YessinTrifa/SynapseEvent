package com.synapseevent.controller;

import com.synapseevent.entities.EventInstance;
import com.synapseevent.service.CustomEventTypeService;
import com.synapseevent.service.EventInstanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.SpinnerValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;

public class CustomEventTypeController {

    @FXML private Label typeTitleLabel;

    @FXML private TableView<EventInstance> eventTable;
    @FXML private TableColumn<EventInstance, Long> idColumn;
    @FXML private TableColumn<EventInstance, String> nameColumn;
    @FXML private TableColumn<EventInstance, String> dateColumn;
    @FXML private TableColumn<EventInstance, String> locationColumn;
    @FXML private TableColumn<EventInstance, String> capacityColumn;
    @FXML private TableColumn<EventInstance, String> priceColumn;
    @FXML private TableColumn<EventInstance, String> statusColumn;
    @FXML private TableColumn<EventInstance, String> descriptionColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> startTimeHourSpinner;
    @FXML private Spinner<Integer> endTimeHourSpinner;
    @FXML private TextField locationField;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;

    private String typeName = "Custom";

    private final EventInstanceService eventInstanceService = new EventInstanceService();
    private final CustomEventTypeService customEventTypeService = new CustomEventTypeService();

    @FXML
    public void initialize() {
        startTimeHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9)
        );
        endTimeHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17)
        );
        capacitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 50)
        );
        priceSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, 0.0, 1)
        );

        statusComboBox.setItems(FXCollections.observableArrayList("draft", "published", "cancelled"));
        statusComboBox.setValue("draft");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""
        ));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        capacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCapacity() != null ? cellData.getValue().getCapacity().toString() : ""
        ));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPrice() != null ? cellData.getValue().getPrice() + " TND" : ""
        ));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadData();
    }

    // Called by AdminDashboardController after loading the FXML
    // to inject the type name before the window is shown
    public void setTypeName(String typeName) {
        this.typeName = typeName;
        if (typeTitleLabel != null) {
            typeTitleLabel.setText(typeName + " Events");
        }
        loadData();
    }

    private void loadData() {
        try {
            java.util.List<EventInstance> all = eventInstanceService.readAll();
            java.util.List<EventInstance> filtered = all.stream()
                    .filter(e -> typeName.equals(e.getType()))
                    .collect(java.util.stream.Collectors.toList());
            eventTable.setItems(FXCollections.observableArrayList(filtered));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();

        if (name == null || name.trim().isEmpty() || date == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Fields");
            alert.setContentText("Name and Date are required.");
            alert.showAndWait();
            return;
        }

        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), 0);
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), 0);
        String location = locationField.getText();
        Integer capacity = capacitySpinner.getValue();
        Double price = priceSpinner.getValue();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";
        String description = descriptionField.getText();

        EventInstance ei = new EventInstance(
                null, name, date, startTime, endTime,
                location, capacity, price,
                "admin@synapse.com", description, status, typeName
        );

        try {
            eventInstanceService.ajouter(ei);
            clearFields();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateEvent() {
        EventInstance selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

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
            eventInstanceService.modifier(selected);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteEvent() {
        EventInstance selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            eventInstanceService.supprimer(selected);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void selectEvent() {
        EventInstance selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        nameField.setText(selected.getName());
        datePicker.setValue(selected.getDate());
        if (selected.getStartTime() != null)
            startTimeHourSpinner.getValueFactory().setValue(selected.getStartTime().getHour());
        if (selected.getEndTime() != null)
            endTimeHourSpinner.getValueFactory().setValue(selected.getEndTime().getHour());
        locationField.setText(selected.getLocation() != null ? selected.getLocation() : "");
        if (selected.getCapacity() != null)
            capacitySpinner.getValueFactory().setValue(selected.getCapacity());
        if (selected.getPrice() != null)
            priceSpinner.getValueFactory().setValue(selected.getPrice());
        statusComboBox.setValue(selected.getStatus());
        descriptionField.setText(selected.getDescription() != null ? selected.getDescription() : "");
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(9);
        endTimeHourSpinner.getValueFactory().setValue(17);
        locationField.clear();
        capacitySpinner.getValueFactory().setValue(50);
        priceSpinner.getValueFactory().setValue(0.0);
        statusComboBox.setValue("draft");
        descriptionField.clear();
    }
}