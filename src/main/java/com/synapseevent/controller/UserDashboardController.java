package com.synapseevent.controller;

import com.synapseevent.entities.*;
import com.synapseevent.service.*;
import com.synapseevent.utils.CurrentUser;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDashboardController {

    @FXML private TabPane categoryTabPane;
    @FXML private TabPane eventsTabPane;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingTypeColumn;
    @FXML private TableColumn<Booking, Long> bookingEventIdColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitRequestButton;

    private EventInstanceService eventInstanceService = new EventInstanceService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();

    private Map<String, List<EventInstance>> eventsByType;

    @FXML
    public void initialize() {
        // Setup bookings table
        bookingTypeColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            String displayName = "Unknown Event";
            if ("instance".equals(booking.getEventType())) {
                try {
                    EventInstance instance = eventInstanceService.findbyId(booking.getEventId());
                    if (instance != null) {
                        displayName = instance.getName();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return new SimpleStringProperty(displayName);
        });
        bookingEventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        eventTypeCombo.getItems().addAll("Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
        loadEvents();
        loadBookings();
    }

    private void loadEvents() {
        try {
            List<EventInstance> instances = eventInstanceService.getPublishedEvents();

            eventsByType = instances.stream()
                .collect(Collectors.groupingBy(EventInstance::getType));

            // Clear existing tabs
            eventsTabPane.getTabs().clear();

            // Create tabs for each type
            for (Map.Entry<String, List<EventInstance>> entry : eventsByType.entrySet()) {
                String typeName = entry.getKey();
                List<EventInstance> typeEvents = entry.getValue();

                Tab typeTab = new Tab(typeName);
                TableView<EventInstance> table = createEventTable();
                table.setItems(FXCollections.observableArrayList(typeEvents));

                VBox vbox = new VBox(table);
                typeTab.setContent(vbox);
                eventsTabPane.getTabs().add(typeTab);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private TableView<EventInstance> createEventTable() {
        TableView<EventInstance> table = new TableView<>();

        TableColumn<EventInstance, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<EventInstance, LocalDate> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<EventInstance, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<EventInstance, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<EventInstance, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<EventInstance, Void> bookColumn = new TableColumn<>("Action");
        bookColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button bookButton = new Button("Book");
            {
                bookButton.setOnAction(event -> {
                    EventInstance ei = getTableView().getItems().get(getIndex());
                    bookEvent(ei);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(bookButton);
                }
            }
        });

        table.getColumns().addAll(nameColumn, dateColumn, locationColumn, priceColumn, descriptionColumn, bookColumn);
        return table;
    }

    private void loadBookings() {
        try {
            bookingsTable.setItems(FXCollections.observableArrayList(bookingService.getBookingsByUser(CurrentUser.getCurrentUser())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
    }

    private void bookEvent(EventInstance ei) {
        Booking booking = new Booking(CurrentUser.getCurrentUser(), "instance", ei.getId(), LocalDate.now(), "pending");
        try {
            bookingService.ajouter(booking);
            loadBookings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void submitCustomRequest() {
        String eventType = eventTypeCombo.getValue();
        LocalDate eventDate = eventDatePicker.getValue();
        String description = descriptionArea.getText();
        if (eventType != null && eventDate != null && description != null && !description.isEmpty()) {
            CustomEventRequest request = new CustomEventRequest(CurrentUser.getCurrentUser(), eventType, eventDate, description, "pending", LocalDate.now());
            try {
                customRequestService.ajouter(request);
                eventTypeCombo.setValue(null);
                eventDatePicker.setValue(null);
                descriptionArea.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void logout() {
        CurrentUser.logout();
        loadFXML("/fxml/login.fxml");
    }

    private void loadFXML(String fxmlPath) {
        try {
            Stage stage = (Stage) categoryTabPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}