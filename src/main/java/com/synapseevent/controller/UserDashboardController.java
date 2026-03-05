package com.synapseevent.controller;

import com.synapseevent.entities.*;
import com.synapseevent.service.*;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.Navigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synapseevent.entities.Review;
import com.synapseevent.service.ReviewService;

public class UserDashboardController {

    @FXML private TabPane categoryTabPane;
    @FXML private TabPane eventsTabPane;

    // Home Tab - Filters
    @FXML private ComboBox<Venue> locationFilterCombo;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private DatePicker dateFromPicker;
    @FXML private DatePicker dateToPicker;

    // Home Tab - Recommended Events Table
    @FXML private TableView<EventInstance> recommendedEventsTable;
    @FXML private TableColumn<EventInstance, String> recommendedNameColumn;
    @FXML private TableColumn<EventInstance, String> recommendedTypeColumn;
    @FXML private TableColumn<EventInstance, LocalDate> recommendedDateColumn;
    @FXML private TableColumn<EventInstance, String> recommendedLocationColumn;
    @FXML private TableColumn<EventInstance, Double> recommendedPriceColumn;
    @FXML private TableColumn<EventInstance, String> recommendedMatchColumn;
    @FXML private TableColumn<EventInstance, Void> recommendedActionColumn;

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
    @FXML private TableColumn<Booking, Void> bookingActionColumn;
    @FXML private TableColumn<Booking, String> bookingRatingColumn;

    // Custom Request Tab
    @FXML private ComboBox<String> eventTypeCombo;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField budgetField;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private ComboBox<String> cityFilterComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button submitRequestButton;
    // My Requests Table
    @FXML private TableView<CustomEventRequest> myRequestsTable;
    @FXML private TableColumn<CustomEventRequest, String>    reqTypeColumn;
    @FXML private TableColumn<CustomEventRequest, LocalDate> reqDateColumn;
    @FXML private TableColumn<CustomEventRequest, Double>    reqBudgetColumn;
    @FXML private TableColumn<CustomEventRequest, Integer>   reqCapacityColumn;
    @FXML private TableColumn<CustomEventRequest, String>    reqLocationColumn;
    @FXML private TableColumn<CustomEventRequest, String>    reqStatusColumn;
    @FXML private TableColumn<CustomEventRequest, LocalDate> reqCreatedColumn;
    
    // Profile Tab Fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField preferredCategoriesCombo;
    @FXML private TextField preferredLocationsCombo;
    @FXML private TextField maxPriceField;
    @FXML private ComboBox<Integer> minRatingCombo;

    // Event Dialog Fields
    @FXML private StackPane eventDialogPane;
    @FXML private Label dialogEventTitle;
    @FXML private Label dialogEventType;
    @FXML private Label dialogEventDate;
    @FXML private Label dialogEventLocation;
    @FXML private Label dialogEventPrice;
    @FXML private Label dialogEventStatus;
    @FXML private TextArea dialogEventDescription;

    // Category Browse Fields
    @FXML private FlowPane eventCardsFlowPane;
    @FXML private Label categoryTitleLabel;
    @FXML private Label categoryEventCountLabel;
    @FXML private Button backToAdminBtn;

    @FXML private StackPane reviewDialogPane;
    @FXML private Label reviewDialogTitle;
    @FXML private ComboBox<Integer> reviewRatingCombo;
    @FXML private TextArea reviewCommentArea;
    private Booking selectedBookingForReview;

    // Sidebar nav buttons
    @FXML private Button userNavHome;
    @FXML private Button userNavBrowse;
    @FXML private Button userNavBookings;
    @FXML private Button userNavRequests;
    @FXML private Button userNavProfile;

    // Page containers
    @FXML private VBox userPageHome;
    @FXML private VBox userPageBookings;
    @FXML private VBox userPageRequests;
    @FXML private VBox userPageProfile;
    @FXML private HBox userPageBrowse;

    @FXML private VBox userPageBrowsePanel;

    // For booking confirmation
    private EventInstance selectedEventForBooking;

    private EventInstanceService eventInstanceService = new EventInstanceService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();
    private VenueService venueService = new VenueService();
    private UserService userService = new UserService();
    private UserPreferencesService userPreferencesService = new UserPreferencesService();
    private CustomEventTypeService customEventTypeService = new CustomEventTypeService();
    private Map<String, List<EventInstance>> eventsByType;
    private List<EventInstance> allPublishedEvents = new ArrayList<>();
    private ReviewService reviewService = new ReviewService();

    @FXML
    public void initialize() {
        // Show "Back to Admin" only if the logged user is admin
        if (backToAdminBtn != null) {
            boolean isAdmin = CurrentUser.isAdmin();
            backToAdminBtn.setVisible(isAdmin);
            backToAdminBtn.setManaged(isAdmin);
        }
        // Setup location filter combo with venues
        setupLocationFilter();

        // Setup type filter combo
        typeFilterCombo.getItems().add("All");
        typeFilterCombo.getItems().addAll("Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
// Add custom event types from DB
        try {
            List<CustomEventType> customTypes = customEventTypeService.readAll();
            for (CustomEventType ct : customTypes) {
                if (ct.getName() != null && !ct.getName().isBlank()) {
                    typeFilterCombo.getItems().add(ct.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        typeFilterCombo.setValue("All");

        // Setup featured events table
        setupFeaturedEventsTable();

        // Setup all events table
        setupAllEventsTable();

        //Step Recommended
        setupRecommendedEventsTable();

        // Setup bookings table
        setupBookingsTable();

        setupMyRequestsTable();
        loadMyRequests();

        //Reviews
        reviewRatingCombo.getItems().addAll(1, 2, 3, 4, 5);

        // Setup custom request combo
        loadEventTypeCombo();
        eventTypeCombo.setOnAction(e -> onEventTypeSelected());

        // Setup venue filter combos
        setupVenueFilters();
        
        // Setup profile tab
        setupProfileTab();

        // Load data
        loadEvents();

        //Load recommended
        loadRecommendedEvents();



        loadBookings();
        allEventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        featuredEventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        showUserHome();
        loadCustomTypeBrowseButtons();
    }

    private void setupProfileTab() {
        // Load current user data
        User current = CurrentUser.getCurrentUser();
        if (current != null) {
            firstNameField.setText(current.getPrenom());
            lastNameField.setText(current.getNom());
            emailField.setText(current.getEmail());
            phoneField.setText(current.getPhone());
            addressField.setText(current.getAddress());
            
            // Make email field read-only
            emailField.setEditable(false);
        }
        
        // Setup preferences combos
        // preferredCategoriesCombo and preferredLocationsCombo are now TextFields
        minRatingCombo.getItems().addAll(1, 2, 3, 4, 5);
        minRatingCombo.setValue(3);

        // Load existing preferences
        loadUserPreferences();
    }
    
    private void loadUserPreferences() {
        User current = CurrentUser.getCurrentUser();
        if (current == null || current.getId() == null) return;
        
        try {
            UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
            if (prefs != null) {
                if (prefs.getPreferredCategories() != null) {
                    preferredCategoriesCombo.setText(prefs.getPreferredCategories());
                }
                if (prefs.getPreferredLocations() != null) {
                    preferredLocationsCombo.setText(prefs.getPreferredLocations());
                }
                if (prefs.getMaxPrice() != null) {
                    maxPriceField.setText(prefs.getMaxPrice().toString());
                }
                if (prefs.getMinRating() != null) {
                    minRatingCombo.setValue(prefs.getMinRating());
                }
                // After loading prefs into the Profile tab combos, also sync the Home tab filter:
                if (prefs.getPreferredCategories() != null && typeFilterCombo != null) {
                    typeFilterCombo.setValue(prefs.getPreferredCategories());
                    applyFilters(); // triggers immediate filter on the All Events table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void updateProfile() {
        User current = CurrentUser.getCurrentUser();
        if (current == null) {
            showAlert("Error", "No user logged in");
            return;
        }
        
        current.setPrenom(firstNameField.getText());
        current.setNom(lastNameField.getText());
        current.setPhone(phoneField.getText());
        current.setAddress(addressField.getText());
        
        try {
            userService.modifier(current);
            CurrentUser.setCurrentUser(current);
            loadRecommendedEvents();
            showAlert("Success", "Profile updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error updating profile: " + e.getMessage());
        }
    }
    
    @FXML
    private void savePreferences() {
        User current = CurrentUser.getCurrentUser();
        if (current == null || current.getId() == null) {
            showAlert("Error", "No user logged in");
            return;
        }
        
        try {
            UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
            boolean isNew = false;
            
            if (prefs == null) {
                prefs = new UserPreferences();
                prefs.setUserId(current.getId());
                isNew = true;
            }
            
            String categories = preferredCategoriesCombo.getText();
            prefs.setPreferredCategories(categories != null && !categories.equals("All") ? categories : null);
            
            String locations = preferredLocationsCombo.getText();
            prefs.setPreferredLocations(locations != null && !locations.equals("All Cities") ? locations : null);
            
            try {
                String priceText = maxPriceField.getText();
                if (priceText != null && !priceText.trim().isEmpty()) {
                    prefs.setMaxPrice(Double.parseDouble(priceText.trim()));
                } else {
                    prefs.setMaxPrice(null);
                }
            } catch (NumberFormatException e) {
                prefs.setMaxPrice(null);
            }
            
            Integer rating = minRatingCombo.getValue();
            prefs.setMinRating(rating);
            
            if (isNew) {
                userPreferencesService.ajouter(prefs);
            } else {
                userPreferencesService.modifier(prefs);
            }
            
            showAlert("Success", "Preferences saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error saving preferences: " + e.getMessage());
        }
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
                bookButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                bookButton.setOnAction(event -> {
                    EventInstance ei = getTableView().getItems().get(getIndex());
                    showEventDetailsDialog(ei);
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
                    showEventDetailsDialog(ei);
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

    private void setupRecommendedEventsTable() {
        recommendedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recommendedTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        recommendedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        recommendedLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        recommendedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Custom match percentage column
            recommendedMatchColumn.setCellFactory(param -> new TableCell<EventInstance, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                   setGraphic(null);
                } else {
                    EventInstance ei = getTableView().getItems().get(getIndex());
                    int match = calculateMatchPercentage(ei);
                    Label matchLabel = new Label(match + "%");
                    matchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (match >= 80 ? "#10b981" : match >= 60 ? "#f59e0b" : "#6b7280"));
                   setGraphic(matchLabel);
               }
            }
        });

        recommendedActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button bookButton = new Button("Book Now");
            {
               bookButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold;");
                bookButton.setOnAction(event -> {
                    EventInstance ei = getTableView().getItems().get(getIndex());
                    showEventDetailsDialog(ei);
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

    private int calculateMatchPercentage(EventInstance ei) {
        int match = 0;
        int factors = 0;
        
        User current = CurrentUser.getCurrentUser();
        if (current == null) return 0;
        
        try {
            UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
            if (prefs != null) {
                // Check category preference
                String[] cats = prefs.getPreferredCategories().split(",");
                for (String cat : cats) {
                    if (ei.getType() != null && ei.getType().equalsIgnoreCase(cat.trim())) {
                        match += 40;
                        break;
                    }
                }
                // Check location preference
                if (prefs.getPreferredLocations() != null && ei.getLocation() != null) {
                    factors++;
                    if (ei.getLocation().toLowerCase().contains(prefs.getPreferredLocations().toLowerCase())) {
                        match += 30;
                    }
                }
                // Check price preference
                if (prefs.getMaxPrice() != null && ei.getPrice() != null) {
                    factors++;
                    if (ei.getPrice() <= prefs.getMaxPrice()) {
                        match += 30;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return factors > 0 ? Math.min(match, 100) : 50; // Default 50% if no preferences set
    }

    private void loadRecommendedEvents() {
        try {
            User current = CurrentUser.getCurrentUser();
            List<EventInstance> recommended = new ArrayList<>();
            
            if (current != null && current.getId() != null) {
                UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
                
                // Get all published events
                List<EventInstance> events = eventInstanceService.getPublishedEvents();
                
                // Filter and score events based on preferences
                for (EventInstance ei : events) {
                    if (ei.getDate() != null && !ei.getDate().isBefore(LocalDate.now())) {
                        int match = calculateMatchPercentage(ei);
                        if (match >= 40) { // Only show events with at least 40% match
                            recommended.add(ei);
                        }
                    }
                }
                
                // Sort by match percentage
                recommended.sort((e1, e2) -> {
                    int m1 = calculateMatchPercentage(e1);
                    int m2 = calculateMatchPercentage(e2);
                    return Integer.compare(m2, m1);
                });
                
                // Limit to top 5
                recommended = recommended.stream().limit(5).collect(Collectors.toList());
            }
            
            // If no recommendations, show upcoming events
            if (recommended.isEmpty()) {
                recommended = allPublishedEvents.stream()
                    .filter(e -> e.getDate() != null && !e.getDate().isBefore(LocalDate.now()))
                    .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
                    .limit(5)
                    .collect(Collectors.toList());
            }
            
            recommendedEventsTable.setItems(FXCollections.observableArrayList(recommended));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Event Details Dialog Methods
    private void showEventDetailsDialog(EventInstance ei) {
        if (ei == null) return;
        
        selectedEventForBooking = ei;
        
        dialogEventTitle.setText(ei.getName() != null ? ei.getName() : "Event Details");
        dialogEventType.setText(ei.getType() != null ? ei.getType() : "-");
        dialogEventDate.setText(ei.getDate() != null ? ei.getDate().toString() : "-");
        dialogEventLocation.setText(ei.getLocation() != null ? ei.getLocation() : "-");
        dialogEventPrice.setText(ei.getPrice() != null ? ei.getPrice() + " TND" : "-");
        dialogEventStatus.setText(ei.getStatus() != null ? ei.getStatus() : "-");
        dialogEventDescription.setText(ei.getDescription() != null ? ei.getDescription() : "No description available.");
        
        eventDialogPane.setVisible(true);
    }

    @FXML
    private void confirmBooking() {
        if (selectedEventForBooking != null) {
            bookEvent(selectedEventForBooking);
            closeEventDialog();
        }
    }

    @FXML
    private void closeEventDialog() {
        eventDialogPane.setVisible(false);
        selectedEventForBooking = null;
    }

    // Quick Recommendation Actions
    @FXML
    private void showRecommendedByLocation() {
        typeFilterCombo.setValue("Partying");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    @FXML
    private void showRecommendedBeach() {
        applyQuickFilter("BEACH");
    }

    @FXML
    private void showRecommendedTeamBuilding() {
        typeFilterCombo.setValue("TeamBuilding");
        applyFilters();
    }

    @FXML
    private void showRecommendedFormation() {
        typeFilterCombo.setValue("Formation");
        applyFilters();
    }

    @FXML
    private void showRecommendedAnniversary() {
        typeFilterCombo.setValue("Anniversary");
        applyFilters();
    }

    private void filterByLocationPreference() {
        try {
            User current = CurrentUser.getCurrentUser();
            if (current != null) {
                UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
                if (prefs != null && prefs.getPreferredLocations() != null) {
                    // Filter events by preferred location
                    String prefLocation = prefs.getPreferredLocations().toLowerCase();
                    List<EventInstance> filtered = allPublishedEvents.stream()
                        .filter(e -> e.getLocation() != null && 
                            e.getLocation().toLowerCase().contains(prefLocation))
                        .collect(Collectors.toList());
                    allEventsTable.setItems(FXCollections.observableArrayList(filtered));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyQuickFilter(String keyword) {
        List<EventInstance> filtered = allPublishedEvents.stream()
            .filter(e -> {
                if (e.getLocation() != null && e.getLocation().toUpperCase().contains(keyword.toUpperCase())) {
                    return true;
                }
                if (e.getType() != null && e.getType().toUpperCase().contains(keyword.toUpperCase())) {
                    return true;
                }
                return false;
            })

            .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
            .collect(Collectors.toList());
        
        allEventsTable.setItems(FXCollections.observableArrayList(filtered));
        
        // Switch to Home tab to show results (if categoryTabPane exists)
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    // Browse by Category Actions
    @FXML
    private void browsePartying() {
        typeFilterCombo.setValue("Partying");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    @FXML
    private void browsePaddle() {
        typeFilterCombo.setValue("Paddle");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    @FXML
    private void browseTeamBuilding() {
        typeFilterCombo.setValue("TeamBuilding");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    @FXML
    private void browseFormation() {
        typeFilterCombo.setValue("Formation");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    @FXML
    private void browseAnniversary() {
        typeFilterCombo.setValue("Anniversary");
        applyFilters();
        if (categoryTabPane != null) {
            categoryTabPane.getSelectionModel().select(0);
        }
    }

    // Category Browse Methods with Visual Cards
    @FXML
    private void showAllCategoryEvents() {
        displayEventCards(allPublishedEvents, "All Events");
    }

    @FXML
    private void showPartyingEvents() {
        List<EventInstance> filtered = filterByType("Partying");
        displayEventCards(filtered, "Partying Events");
    }

    @FXML
    private void showPaddleEvents() {
        List<EventInstance> filtered = filterByType("Paddle");
        displayEventCards(filtered, "Paddle Events");
    }

    @FXML
    private void showTeamBuildingEvents() {
        List<EventInstance> filtered = filterByType("TeamBuilding");
        displayEventCards(filtered, "Team Building Events");
    }

    @FXML
    private void showFormationEvents() {
        List<EventInstance> filtered = filterByType("Formation");
        displayEventCards(filtered, "Formation Events");
    }

    @FXML
    private void showAnniversaryEvents() {
        List<EventInstance> filtered = filterByType("Anniversary");
        displayEventCards(filtered, "Anniversary Events");
    }

    private List<EventInstance> filterByType(String type) {
        return allPublishedEvents.stream()
                .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase(type))
                .sorted((e1, e2) -> {
                    if (e1.getDate() == null) return 1;
                    if (e2.getDate() == null) return -1;
                    return e1.getDate().compareTo(e2.getDate());
                })
                .collect(Collectors.toList());
    }
    private void displayEventCards(List<EventInstance> events, String title) {
        categoryTitleLabel.setText(title);
        categoryEventCountLabel.setText(events.size() + " events found");
        eventCardsFlowPane.getChildren().clear();

        if (events.isEmpty()) {
            Label noEventsLabel = new Label("No events found in this category.\nCheck back later or browse other categories!");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #64748b; -fx-padding: 40;");
            noEventsLabel.setTextAlignment(TextAlignment.CENTER);
            eventCardsFlowPane.getChildren().add(noEventsLabel);
            return;
        }

        for (EventInstance ei : events) {
            VBox card = createEventCard(ei);
            eventCardsFlowPane.getChildren().add(card);
        }

        // Switch to Browse by Type tab
        showUserBrowse();
    }

    private VBox createEventCard(EventInstance ei) {
        // Determine card color based on event type
        String cardColor = getCardColor(ei.getType());
        String emoji = getEventEmoji(ei.getType());

        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(280);
        card.setStyle(
                "-fx-background-color: #f0e0b0;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: #8a6a20;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                        "-fx-padding: 0;"
        );

        // Header with gradient
        Label header = new Label(emoji + " " + (ei.getType() != null ? ei.getType() : "Event"));
        header.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15; " +
            "-fx-background-color: " + cardColor + "; " +
            "-fx-background-radius: 15 15 0 0;"
        );
        header.setPrefHeight(50);
        header.setAlignment(Pos.CENTER_LEFT);

        // Event Name
        Label nameLabel = new Label(ei.getName() != null ? ei.getName() : "Unnamed Event");
        nameLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #1a3a1a; -fx-font-family: Georgia; -fx-padding: 12 14 4 14;");
        nameLabel.setWrapText(true);

        // Date with icon
        Label dateLabel = new Label("Date: " + (ei.getDate() != null ? ei.getDate().toString() : "TBD"));
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #5a4010; -fx-padding: 4 14;");
        // Location with icon
        Label locationLabel = new Label("Location: " + (ei.getLocation() != null ? ei.getLocation() : "TBD"));
        locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #5a4010; -fx-padding: 4 14;");
        locationLabel.setWrapText(true);

        // Price
        Label priceLabel = new Label("Price: " + (ei.getPrice() != null ? ei.getPrice() + " TND" : "Free"));
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #7a1a2a; -fx-padding: 8 14;");

        // Status badge
        Label statusLabel = new Label(ei.getStatus() != null ? ei.getStatus().toUpperCase() : "AVAILABLE");
        String statusColor = "published".equalsIgnoreCase(ei.getStatus()) ? "#1a4a1a" :
                "available".equalsIgnoreCase(ei.getStatus()) ? "#1a4a1a" :
                        "pending".equalsIgnoreCase(ei.getStatus()) ? "#8a3a00" : "#5a4010";
        statusLabel.setStyle(
                "-fx-font-size: 11;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #e8d5a3;" +
                        "-fx-background-color: " + statusColor + ";" +
                        "-fx-padding: 4 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #c8a040;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;"
        );
        HBox statusBox = new HBox(statusLabel);
        statusBox.setStyle("-fx-padding: 0 14 8 14;");
        // Book Button
        Button bookBtn = new Button("Book Now");
        bookBtn.setStyle(
                "-fx-background-color: " + cardColor + ";" +
                        "-fx-text-fill: #e8d5a3;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-color: #c8a040;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 999;" +
                        "-fx-cursor: hand;"
        );
        bookBtn.setPrefHeight(36);
        bookBtn.setOnAction(e -> showEventDetailsDialog(ei));

        VBox content = new VBox(nameLabel, dateLabel, locationLabel, priceLabel, statusBox, bookBtn);
        content.setStyle("-fx-padding: 0;");

        card.getChildren().addAll(header, content);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: #f5ead8;" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-radius: 14;" +
                            "-fx-border-color: " + cardColor + ";" +
                            "-fx-border-width: 2;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 14, 0, 0, 4);" +
                            "-fx-padding: 0;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: #f0e0b0;" +
                            "-fx-background-radius: 14;" +
                            "-fx-border-radius: 14;" +
                            "-fx-border-color: #8a6a20;" +
                            "-fx-border-width: 1.5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                            "-fx-padding: 0;"
            );
        });

        return card;
    }

    private String getCardColor(String type) {
        if (type == null) return "#7a1a2a";
        switch (type) {
            case "Partying":     return "#7a1a2a";
            case "Paddle":       return "#1a5a6a";
            case "TeamBuilding": return "#1a4a1a";
            case "Formation":    return "#6c4a10";
            case "Anniversary":  return "#8a3a00";
            default:             return "#5a3a6a";
        }
    }

    private String getEventEmoji(String type) {
        if (type == null) return "EVENT";
        switch (type) {
            case "Partying": return "PARTY";
            case "Paddle": return "PADDLE";
            case "TeamBuilding": return "TEAM";
            case "Formation": return "TRAINING";
            case "Anniversary": return "ANNIV";
            default: return "EVENT";
        }
    }
    private void setupMyRequestsTable() {
        reqTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        reqDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        reqBudgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));
        reqCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        reqLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        reqStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        reqCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        // Color-code the status cell
        reqStatusColumn.setCellFactory(col -> new TableCell<CustomEventRequest, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status.toLowerCase()) {
                        case "pending":
                            setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                            break;
                        case "approved":
                            setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                            break;
                        case "rejected":
                            setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void loadMyRequests() {
        User current = CurrentUser.getCurrentUser();
        if (current == null || current.getId() == null) {
            myRequestsTable.setItems(FXCollections.observableArrayList());
            return;
        }
        try {
            List<CustomEventRequest> requests = customRequestService.getRequestsByUserId(current.getId());
            myRequestsTable.setItems(FXCollections.observableArrayList(requests));
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        bookingRatingColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            try {
                double avg = reviewService.getAverageRating(booking.getEventType(), booking.getEventId());
                String display = avg == 0.0 ? "No reviews" : String.format("Rating %.1f / 5", avg);
                return new SimpleStringProperty(display);
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("-");
            }
        });
        bookingActionColumn.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button reviewBtn = new Button("Review");
            {
                reviewBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;");
                reviewBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    openReviewDialog(booking);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : reviewBtn);
            }
        });
    }

    private void setupLocationFilter() {
        try {
            locationFilterCombo.getItems().clear();
            locationFilterCombo.getItems().add(null); // Allow "no selection" / All venues
            locationFilterCombo.getItems().addAll(venueService.readAll());
            locationFilterCombo.setValue(null);
            locationFilterCombo.setPromptText("Select venue...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        Venue selectedVenue = locationFilterCombo.getValue();
        String type = typeFilterCombo.getValue();
        LocalDate fromDate = dateFromPicker.getValue();
        LocalDate toDate = dateToPicker.getValue();

        List<EventInstance> filtered = allPublishedEvents.stream()
            .filter(e -> {
                // Filter by venue
                if (selectedVenue != null) {
                    String eventLocation = e.getLocation() != null ? e.getLocation().toLowerCase() : "";
                    String venueName = selectedVenue.getName() != null ? selectedVenue.getName().toLowerCase() : "";
                    String venueAddress = selectedVenue.getAddress() != null ? selectedVenue.getAddress().toLowerCase() : "";
                    if (!eventLocation.contains(venueName) && !eventLocation.contains(venueAddress)) {
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
        if (locationFilterCombo != null) {
            locationFilterCombo.setValue(null);
        }
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
                bookButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
                bookButton.setOnAction(event -> {
                    EventInstance ei = getTableView().getItems().get(getIndex());
                    showEventDetailsDialog(ei);
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
            User current = CurrentUser.getCurrentUser();
            if (current == null || current.getId() == null) {
                bookingsTable.setItems(FXCollections.observableArrayList());
                return;
            }
            bookingsTable.setItems(FXCollections.observableArrayList(bookingService.getBookingsByUser(current)));
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

    private void loadEventTypeCombo() {
        // Start with the built-in types
        List<String> types = new ArrayList<>();
        types.addAll(java.util.Arrays.asList("Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding"));

        // Append any custom types from the database
        try {
            List<CustomEventType> customTypes = customEventTypeService.readAll();
            for (CustomEventType ct : customTypes) {
                if (ct.getName() != null && !ct.getName().isBlank()) {
                    types.add(ct.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        eventTypeCombo.getItems().setAll(types);
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
        User current = CurrentUser.getCurrentUser();
        if (current == null || current.getId() == null) {
            showAlert("Error", "You must be logged in to book an event.");
            return;
        }
        if (ei == null || ei.getId() == null) {
            showAlert("Error", "Invalid event.");
            return;
        }

        Booking booking = new Booking(current.getId(), ei.getType(), ei.getId(), LocalDate.now(), "pending");
        booking.setUser(current);

        try {
            bookingService.ajouter(booking);

            ei.setStatus("pending");
            eventInstanceService.modifier(ei);

            loadEvents();
            loadRecommendedEvents();
            loadBookings();
            
            showAlert("Booking Confirmed! 🎉", 
                "You have successfully booked '" + ei.getName() + "'!\n\n" +
                "Event Date: " + ei.getDate() + "\n" +
                "Location: " + ei.getLocation() + "\n\n" +
                "Check 'My Bookings' tab for details.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error booking event: " + e.getMessage());
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

        String rawValue = capacityField.getText().trim();
        if (rawValue.isEmpty()) {
            // either show an error alert to the user, or default to 0
            showAlert("Validation Error", "Please enter a valid number for capacity.");
            return;
        }
        int capacity = Integer.parseInt(rawValue);
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
                capacityField.setText("50");
                venueComboBox.setValue(null);
                venueTypeFilterComboBox.setValue("All Types");
                cityFilterComboBox.setValue("All Cities");
                filterVenues();
                descriptionArea.clear();
                showAlert("Success", "Your custom event request has been submitted successfully!");
                loadMyRequests();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Error submitting request: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Please fill in all required fields (Event Type and Date)");
        }
    }

    @FXML
    private void goToAdminDashboard() {
        try {
            Navigator.get().go("/fxml/adminDashboard.fxml", "Admin Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error opening admin dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            CurrentUser.logout();
            Navigator.get().go("/fxml/login.fxml", "Login – SynapseEvent");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Error during logout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void openReviewDialog(Booking booking) {
        if (booking == null) return;
        selectedBookingForReview = booking;

        // Set the title to show which event is being reviewed
        try {
            EventInstance instance = eventInstanceService.findbyId(booking.getEventId());
            String name = instance != null && instance.getName() != null ? instance.getName() : "Event #" + booking.getEventId();
            reviewDialogTitle.setText("Review: " + name);
        } catch (SQLException e) {
            reviewDialogTitle.setText("Leave a Review");
        }

        reviewRatingCombo.setValue(5);
        reviewCommentArea.clear();
        reviewDialogPane.setVisible(true);
    }
    @FXML
    private void submitReview() {
        if (selectedBookingForReview == null) return;

        Integer rating = reviewRatingCombo.getValue();
        String comment = reviewCommentArea.getText();

        if (rating == null) {
            showAlert("Error", "Please select a rating.");
            return;
        }

        User current = CurrentUser.getCurrentUser();
        if (current == null || current.getId() == null) {
            showAlert("Error", "You must be logged in to leave a review.");
            return;
        }

        Review review = new Review(
                current.getId(),
                selectedBookingForReview.getEventType(),
                selectedBookingForReview.getEventId(),
                rating,
                comment
        );

        try {
            reviewService.ajouter(review);
            closeReviewDialog();
            loadBookings(); // refresh the table so avg rating updates
            showAlert("Thank you! 🌟", "Your review has been submitted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit review: " + e.getMessage());
        }
    }
    @FXML
    private void closeReviewDialog() {
        reviewDialogPane.setVisible(false);
        selectedBookingForReview = null;
    }
    // ── Sidebar navigation ──────────────────────────────────────
    private void activateUserNav(Button active) {
        Button[] all = {userNavHome, userNavBrowse, userNavBookings, userNavRequests, userNavProfile};
        for (Button b : all) {
            if (b != null) b.getStyleClass().remove("user-nav-btn-active");
        }
        if (active != null) active.getStyleClass().add("user-nav-btn-active");
    }

    private void showUserPage(javafx.scene.Node page) {
        javafx.scene.Node[] pages = {userPageHome, userPageBrowse, userPageBookings, userPageRequests, userPageProfile};
        for (javafx.scene.Node p : pages) {
            if (p != null) { p.setVisible(false); p.setManaged(false); }
        }
        if (page != null) { page.setVisible(true); page.setManaged(true); }
    }

    @FXML private void showUserHome()     { activateUserNav(userNavHome);     showUserPage(userPageHome); }
    @FXML private void showUserBrowse()   { activateUserNav(userNavBrowse);   showUserPage(userPageBrowse); }
    @FXML private void showUserBookings() { activateUserNav(userNavBookings); showUserPage(userPageBookings); }
    @FXML private void showUserRequests() { activateUserNav(userNavRequests); showUserPage(userPageRequests); }
    @FXML private void showUserProfile()  { activateUserNav(userNavProfile);  showUserPage(userPageProfile); }
    
    // ── Réservation Padel Integration ──────────────────────────────────────
    public void openPadelReservation() {
        // Navigate to Padel Reservation Dashboard
        Navigator.get().go("/fxml/reservationPadelDashboard.fxml", "Réservation Padel");
    }
    
    private void loadCustomTypeBrowseButtons() {
        if (userPageBrowsePanel == null) return;
        try {
            List<CustomEventType> customTypes = customEventTypeService.readAll();
            for (CustomEventType ct : customTypes) {
                if (ct.getName() == null || ct.getName().isBlank()) continue;
                String typeName = ct.getName();
                Button btn = new Button(typeName);
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.getStyleClass().add("user-browse-btn");
                btn.setOnAction(e -> {
                    List<EventInstance> filtered = filterByType(typeName);
                    displayEventCards(filtered, typeName + " Events");
                });
                userPageBrowsePanel.getChildren().add(btn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

