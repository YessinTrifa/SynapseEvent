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

    // Home Tab - Filters
    @FXML private TextField locationSearchField;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private DatePicker dateFromPicker;
    @FXML private DatePicker dateToPicker;

    // Home Tab - Featured Events Table
    @FXML private TableView<EventInstance> featuredEventsTable;
    @FXML private TableColumn<EventInstance, String> featuredNameColumn;
    @FXML private TableColumn<EventInstance, String> featuredTypeColumn;
    @FXML private TableColumn<EventInstance, LocalDate> featuredDateColumn;
    @FXML private TableColumn<EventInstance, String> featuredLocationColumn;
    @FXML private TableColumn<EventInstance, Double> featuredPriceColumn;
    @FXML private TableColumn<EventInstance, Void> featuredActionColumn;

    // Home Tab - All Events Table
    @FXML private TableView<EventInstance> allEventsTable;
    @FXML private TableColumn<EventInstance, String> allNameColumn;
    @FXML private TableColumn<EventInstance, String> allTypeColumn;
    @FXML private TableColumn<EventInstance, LocalDate> allDateColumn;
    @FXML private TableColumn<EventInstance, String> allLocationColumn;
    @FXML private TableColumn<EventInstance, Double> allPriceColumn;
    @FXML private TableColumn<EventInstance, String> allStatusColumn;
    @FXML private TableColumn<EventInstance, Void> allActionColumn;

    // My Bookings Tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingTypeColumn;
    @FXML private TableColumn<Booking, Long> bookingEventIdColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;

    // Custom Request Tab
    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField budgetField;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private ComboBox<String> cityFilterComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitRequestButton;

    private EventInstanceService eventInstanceService = new EventInstanceService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();
    private VenueService venueService = new VenueService();

    private Map<String, List<EventInstance>> eventsByType;
    private List<EventInstance> allPublishedEvents = new ArrayList<>();

    @FXML
    public void initialize() {
        // Setup type filter combo
        typeFilterCombo.getItems().addAll("All", "Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
        typeFilterCombo.setValue("All");

        // Setup featured events table
        setupFeaturedEventsTable();

        // Setup all events table
        setupAllEventsTable();

        // Setup bookings table
        setupBookingsTable();

        // Setup custom request combo
        eventTypeCombo.getItems().addAll("Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
        eventTypeCombo.setOnAction(e -> onEventTypeSelected());

        // Setup venue filter combos
        setupVenueFilters();

        // Load data
        loadEvents();
        loadBookings();
    }

    private void setupFeaturedEventsTable() {
        featuredNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        featuredTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        featuredDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        featuredLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        featuredPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        featuredActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
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
    }

    private void setupAllEventsTable() {
        allNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        allTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        allDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        allLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        allPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        allStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        allActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
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
    }

    private void setupBookingsTable() {
        bookingTypeColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            String displayName = "Unknown Event";
            try {
                EventInstance instance = eventInstanceService.findbyId(booking.getEventId());
                if (instance != null) {
                    String typeName = instance.getType() != null ? instance.getType() : "Event";
                    String eventName = instance.getName() != null ? instance.getName() : "Unnamed";
                    displayName = typeName + " - " + eventName;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty(displayName);
        });
        bookingEventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadEvents() {
        try {
            allPublishedEvents = eventInstanceService.getPublishedEvents();

            // Load featured events (upcoming events sorted by date)
            List<EventInstance> featured = allPublishedEvents.stream()
                .filter(e -> e.getDate() != null && !e.getDate().isBefore(LocalDate.now()))
                .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                .limit(5)
                .collect(Collectors.toList());
            featuredEventsTable.setItems(FXCollections.observableArrayList(featured));

            // Load all events
            allEventsTable.setItems(FXCollections.observableArrayList(allPublishedEvents));

            // Load events by type for legacy tabs
            eventsByType = allPublishedEvents.stream()
                .collect(Collectors.groupingBy(EventInstance::getType));

            // Clear existing tabs
            if (eventsTabPane != null) {
                eventsTabPane.getTabs().clear();
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void applyFilters() {
        String location = locationSearchField.getText().trim().toLowerCase();
        String type = typeFilterCombo.getValue();
        LocalDate fromDate = dateFromPicker.getValue();
        LocalDate toDate = dateToPicker.getValue();

        List<EventInstance> filtered = allPublishedEvents.stream()
            .filter(e -> {
                // Filter by location
                if (!location.isEmpty()) {
                    String eventLocation = e.getLocation() != null ? e.getLocation().toLowerCase() : "";
                    if (!eventLocation.contains(location)) {
                        return false;
                    }
                }
                // Filter by type
                if (type != null && !type.equals("All")) {
                    if (!type.equals(e.getType())) {
                        return false;
                    }
                }
                // Filter by date range
                if (fromDate != null && e.getDate() != null) {
                    if (e.getDate().isBefore(fromDate)) {
                        return false;
                    }
                }
                if (toDate != null && e.getDate() != null) {
                    if (e.getDate().isAfter(toDate)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());

        allEventsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void clearFilters() {
        locationSearchField.clear();
        typeFilterCombo.setValue("All");
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        allEventsTable.setItems(FXCollections.observableArrayList(allPublishedEvents));
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
    
    private void setupVenueFilters() {
        // Set up venue type filter
        venueTypeFilterComboBox.getItems().addAll("All Types", "CLUB", "BEACH", "HOTEL", "RESTAURANT");
        venueTypeFilterComboBox.setValue("All Types");
        
        // Set up city filter
        cityFilterComboBox.getItems().add("All Cities");
        try {
            cityFilterComboBox.getItems().addAll(venueService.getAllCities());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cityFilterComboBox.setValue("All Cities");
        
        // Set up venue combo box and load all venues
        loadVenues();
        
        // Add listeners for filtering
        venueTypeFilterComboBox.setOnAction(e -> filterVenues());
        cityFilterComboBox.setOnAction(e -> filterVenues());
    }
    
    private void loadVenues() {
        try {
            venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void filterVenues() {
        String type = venueTypeFilterComboBox.getValue();
        String city = cityFilterComboBox.getValue();
        try {
            venueComboBox.setItems(FXCollections.observableArrayList(venueService.findByTypeAndCity(type, city)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void onEventTypeSelected() {
        String eventType = eventTypeCombo.getValue();
        if (eventType != null) {
            // Map event type to suggested venue types
            switch (eventType) {
                case "Anniversary":
                case "Partying":
                    venueTypeFilterComboBox.getItems().setAll("All Types", "CLUB", "HOTEL", "RESTAURANT", "BEACH");
                    break;
                case "Formation":
                    venueTypeFilterComboBox.getItems().setAll("All Types", "HOTEL", "RESTAURANT", "CLUB");
                    break;
                case "TeamBuilding":
                    venueTypeFilterComboBox.getItems().setAll("All Types", "BEACH", "HOTEL", "RESTAURANT");
                    break;
                case "Paddle":
                    venueTypeFilterComboBox.getItems().setAll("All Types", "BEACH", "CLUB");
                    break;
                default:
                    venueTypeFilterComboBox.getItems().setAll("All Types", "CLUB", "BEACH", "HOTEL", "RESTAURANT");
            }
            venueTypeFilterComboBox.setValue("All Types");
            filterVenues();
        }
    }

    private void bookEvent(EventInstance ei) {
        Booking booking = new Booking(CurrentUser.getCurrentUser(), "instance", ei.getId(), LocalDate.now(), "pending");
        try {
            bookingService.ajouter(booking);

            // Update event instance status to "pending" when a user books it
            ei.setStatus("pending");
            eventInstanceService.modifier(ei);

            // Refresh the tables to show updated status
            loadEvents();
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
        
        // Get new fields
        Double budget = null;
        if (budgetField.getText() != null && !budgetField.getText().trim().isEmpty()) {
            try {
                budget = Double.parseDouble(budgetField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid budget format. Please enter a valid number.");
                return;
            }
        }
        
        Integer capacity = capacitySpinner.getValue();
        Venue selectedVenue = venueComboBox.getValue();
        String location = selectedVenue != null ? selectedVenue.getName() + " (" + selectedVenue.getAddress() + ")" : "";
        
        if (eventType != null && eventDate != null) {
            CustomEventRequest request = new CustomEventRequest();
            request.setUser(CurrentUser.getCurrentUser());
            request.setEventType(eventType);
            request.setEventDate(eventDate);
            request.setBudget(budget);
            request.setCapacity(capacity);
            request.setLocation(location);
            request.setDescription(description);
            request.setStatus("pending");
            request.setCreatedDate(LocalDate.now());
            
            try {
                customRequestService.ajouter(request);
                eventTypeCombo.setValue(null);
                eventDatePicker.setValue(null);
                budgetField.clear();
                capacitySpinner.getValueFactory().setValue(50);
                venueComboBox.setValue(null);
                venueTypeFilterComboBox.setValue("All Types");
                cityFilterComboBox.setValue("All Cities");
                filterVenues();
                descriptionArea.clear();
                showAlert("Success", "Your custom event request has been submitted successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Error submitting request: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Please fill in all required fields (Event Type and Date)");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
