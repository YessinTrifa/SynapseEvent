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
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardController {

    @FXML private TableView<EventSummary> eventsTable;
    @FXML private TableColumn<EventSummary, String> eventTypeColumn;
    @FXML private TableColumn<EventSummary, String> eventNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> eventDateColumn;
    @FXML private TableColumn<EventSummary, String> eventDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> bookColumn;

    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingTypeColumn;
    @FXML private TableColumn<Booking, Long> bookingEventIdColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitRequestButton;

    private AnniversaryEventService anniversaryService = new AnniversaryEventService();
    private FormationEventService formationService = new FormationEventService();
    private PaddleEventService paddleService = new PaddleEventService();
    private PartyingEventService partyingService = new PartyingEventService();
    private TeamBuildingEventService teamBuildingService = new TeamBuildingEventService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();

    @FXML
    public void initialize() {
        // Setup events table
        eventTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        bookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
            private final Button bookButton = new Button("Book");

            {
                bookButton.setOnAction(event -> {
                    EventSummary es = getTableView().getItems().get(getIndex());
                    bookEvent(es);
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

        // Setup bookings table
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        bookingEventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        eventTypeCombo.getItems().addAll("Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
        loadEvents();
        loadBookings();
    }

    private void loadEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (AnniversaryEvent e : anniversaryService.getPublishedEvents()) {
                events.add(new EventSummary("Anniversary", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
            for (FormationEvent e : formationService.getPublishedEvents()) {
                events.add(new EventSummary("Formation", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
            for (PaddleEvent e : paddleService.getPublishedEvents()) {
                events.add(new EventSummary("Paddle", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
            for (PartyingEvent e : partyingService.getPublishedEvents()) {
                events.add(new EventSummary("Partying", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
            for (TeamBuildingEvent e : teamBuildingService.getPublishedEvents()) {
                events.add(new EventSummary("TeamBuilding", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        eventsTable.setItems(FXCollections.observableArrayList(events));
    }

    private void loadBookings() {
        try {
            bookingsTable.setItems(FXCollections.observableArrayList(bookingService.getBookingsByUser(CurrentUser.getCurrentUser())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
    }

    private void bookEvent(EventSummary es) {
        Booking booking = new Booking(CurrentUser.getCurrentUser(), es.getType().toLowerCase(), es.getId(), LocalDate.now(), "pending");
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
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}