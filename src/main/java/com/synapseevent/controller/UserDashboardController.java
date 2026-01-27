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

    @FXML private TableView<EventSummary> anniversaryTable;
    @FXML private TableColumn<EventSummary, String> anniversaryNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> anniversaryDateColumn;
    @FXML private TableColumn<EventSummary, String> anniversaryDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> anniversaryBookColumn;

    @FXML private TableView<EventSummary> formationTable;
    @FXML private TableColumn<EventSummary, String> formationNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> formationDateColumn;
    @FXML private TableColumn<EventSummary, String> formationDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> formationBookColumn;

    @FXML private TableView<EventSummary> paddleTable;
    @FXML private TableColumn<EventSummary, String> paddleNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> paddleDateColumn;
    @FXML private TableColumn<EventSummary, String> paddleDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> paddleBookColumn;

    @FXML private TableView<EventSummary> partyingTable;
    @FXML private TableColumn<EventSummary, String> partyingNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> partyingDateColumn;
    @FXML private TableColumn<EventSummary, String> partyingDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> partyingBookColumn;

    @FXML private TableView<EventSummary> teamBuildingTable;
    @FXML private TableColumn<EventSummary, String> teamBuildingNameColumn;
    @FXML private TableColumn<EventSummary, LocalDate> teamBuildingDateColumn;
    @FXML private TableColumn<EventSummary, String> teamBuildingDescriptionColumn;
    @FXML private TableColumn<EventSummary, Void> teamBuildingBookColumn;

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
        // Setup anniversary table
        anniversaryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        anniversaryDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        anniversaryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        anniversaryBookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
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

        // Setup formation table
        formationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        formationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        formationBookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
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

        // Setup paddle table
        paddleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        paddleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        paddleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        paddleBookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
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

        // Setup partying table
        partyingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partyingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        partyingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        partyingBookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
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

        // Setup teamBuilding table
        teamBuildingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamBuildingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        teamBuildingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        teamBuildingBookColumn.setCellFactory(param -> new TableCell<EventSummary, Void>() {
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
        loadAnniversaryEvents();
        loadFormationEvents();
        loadPaddleEvents();
        loadPartyingEvents();
        loadTeamBuildingEvents();
        loadBookings();
    }

    private void loadAnniversaryEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (AnniversaryEvent e : anniversaryService.getPublishedEvents()) {
                events.add(new EventSummary("Anniversary", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        anniversaryTable.setItems(FXCollections.observableArrayList(events));
    }

    private void loadFormationEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (FormationEvent e : formationService.getPublishedEvents()) {
                events.add(new EventSummary("Formation", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        formationTable.setItems(FXCollections.observableArrayList(events));
    }

    private void loadPaddleEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (PaddleEvent e : paddleService.getPublishedEvents()) {
                events.add(new EventSummary("Paddle", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        paddleTable.setItems(FXCollections.observableArrayList(events));
    }

    private void loadPartyingEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (PartyingEvent e : partyingService.getPublishedEvents()) {
                events.add(new EventSummary("Partying", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        partyingTable.setItems(FXCollections.observableArrayList(events));
    }

    private void loadTeamBuildingEvents() {
        List<EventSummary> events = new ArrayList<>();
        try {
            for (TeamBuildingEvent e : teamBuildingService.getPublishedEvents()) {
                events.add(new EventSummary("TeamBuilding", e.getId(), e.getName(), e.getDate(), e.getDescription()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        teamBuildingTable.setItems(FXCollections.observableArrayList(events));
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
            Stage stage = (Stage) anniversaryTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}