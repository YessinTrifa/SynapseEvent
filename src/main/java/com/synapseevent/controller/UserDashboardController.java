package com.synapseevent.controller;

import com.synapseevent.entities.*;
import com.synapseevent.service.*;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    @FXML private Label userTopTitle;

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
        
        // Check if we should show a specific page based on previous navigation
        String previousPage = EventContext.getPreviousPage();
        if ("courts".equals(previousPage)) {
            // User came from court booking - show paddle courts
            EventContext.setPreviousPage(null); // Clear it
            showUserBrowse(); // First show the browse tab
            showPaddleCourts(); // Then show the courts
        } else {
            showUserHome();
        }
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
        // Show partying options instead of directly showing events
        displayPartyingOptions();
    }
    
    private void displayPartyingOptions() {
        categoryTitleLabel.setText("Partying - Choose an option");
        categoryEventCountLabel.setText("Select how you want to browse party events");
        eventCardsFlowPane.getChildren().clear();

        // Option 1: See All Events
        VBox eventsCard = createPartyingOptionCard(
            "🎉", 
            "See All Events", 
            "Browse all available party events in the city",
            "-fx-background-color: #fef3c7; -fx-border-color: #f59e0b;"
        );
        eventsCard.setOnMouseClicked(e -> {
            List<EventInstance> filtered = filterByType("Partying");
            displayEventCards(filtered, "Partying Events - All Available");
        });

        // Option 2: Browse by Venue
        VBox venueCard = createPartyingOptionCard(
            "📍", 
            "Browse by Venue", 
            "Find parties at specific venues",
            "-fx-background-color: #e0f2fe; -fx-border-color: #0284c7;"
        );
        venueCard.setOnMouseClicked(e -> {
            showPartyingByVenue();
        });

        // Option 3: Browse by Type of Party
        VBox typeCard = createPartyingOptionCard(
            "🎊", 
            "Browse by Type of Party", 
            "Explore parties by theme or style",
            "-fx-background-color: #fce7f3; -fx-border-color: #db2777;"
        );
        typeCard.setOnMouseClicked(e -> {
            showPartyingByType();
        });

        eventCardsFlowPane.getChildren().addAll(eventsCard, venueCard, typeCard);
        showUserBrowse();
    }
    
    private VBox createPartyingOptionCard(String icon, String title, String description, String style) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(180);
        card.setStyle(
            style +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;"
        );

        // Header with icon
        Label header = new Label(icon + " " + title);
        header.setStyle(
            "-fx-font-size: 18; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937; " +
            "-fx-padding: 25;"
        );
        header.setAlignment(Pos.CENTER);
        header.setTextAlignment(TextAlignment.CENTER);

        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-text-fill: #4b5563; " +
            "-fx-padding: 0 20 20 20;"
        );
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setWrapText(true);

        card.getChildren().addAll(header, descLabel);
        return card;
    }
    
    private void showPartyingByVenue() {
        // Filter partying events by venue
        List<EventInstance> partyingEvents = filterByType("Partying");
        
        // Group by venue (location)
        Map<String, List<EventInstance>> eventsByVenue = partyingEvents.stream()
            .collect(Collectors.groupingBy(EventInstance::getLocation));
        
        categoryTitleLabel.setText("Partying - Browse by Venue");
        categoryEventCountLabel.setText("Select a venue to see parties");
        eventCardsFlowPane.getChildren().clear();

        for (Map.Entry<String, List<EventInstance>> entry : eventsByVenue.entrySet()) {
            String venue = entry.getKey();
            List<EventInstance> events = entry.getValue();
            
            VBox venueCard = createVenueCard(venue, events.size());
            final String venueName = venue;
            venueCard.setOnMouseClicked(e -> {
                displayEventCards(events, "Partying at " + venueName);
            });
            eventCardsFlowPane.getChildren().add(venueCard);
        }
        
        if (eventsByVenue.isEmpty()) {
            Label noVenuesLabel = new Label("No venues found for party events");
            noVenuesLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280;");
            eventCardsFlowPane.getChildren().add(noVenuesLabel);
        }
        
        showUserBrowse();
    }
    
    private VBox createVenueCard(String venueName, int eventCount) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(120);
        card.setStyle(
            "-fx-background-color: #e0f2fe; " +
            "-fx-border-color: #0284c7; " +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 20;" +
            "-fx-cursor: hand;"
        );

        Label header = new Label("📍 " + venueName);
        header.setStyle(
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937;"
        );

        Label countLabel = new Label(eventCount + " events available");
        countLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-text-fill: #6b7280; " +
            "-fx-padding: 10 0 0 0;"
        );

        card.getChildren().addAll(header, countLabel);
        return card;
    }
    
    private void showPartyingByType() {
        // Browse by type of party (theme/music type)
        categoryTitleLabel.setText("Partying - Browse by Type of Party");
        categoryEventCountLabel.setText("Select a party type to explore");
        eventCardsFlowPane.getChildren().clear();

        // Party types/themes available
        String[] partyTypes = {
            "Birthday Party",
            "New Year's Eve",
            "Holiday Celebration",
            "Latin Night",
            "Retro Night (80s/90s)",
            "Open Mic Night",
            "Corporate Gala",
            "Graduation Party",
            "Bachelor/Bachelorette",
            "Summer Bash",
            "Halloween",
            "Ladies Night",
            "Gentlemen's Night",
            "Karaoke Night",
            "Bonfire Party"
        };

        String[] partyIcons = {
            "🎂", "🎆", "🎄", "💃", "🕺", "🎤", "👔", "🎓", "💍", "🌴", "🎃", "💜", "🤵", "🔥", "🎤"
        };

        for (int i = 0; i < partyTypes.length; i++) {
            String partyType = partyTypes[i];
            String icon = partyIcons[i];
            
            VBox typeCard = createPartyTypeCard(icon, partyType);
            final String type = partyType;
            typeCard.setOnMouseClicked(evt -> {
                // Filter events by this party type
                List<EventInstance> allPartying = filterByType("Partying");
                List<EventInstance> filtered = allPartying.stream()
                    .filter(ev -> ev.getDescription() != null && 
                        (ev.getDescription().contains(type) || 
                         (ev.getName() != null && ev.getName().contains(type))))
                    .collect(Collectors.toList());
                
                if (filtered.isEmpty()) {
                    // If no specific match, show all partying events
                    displayEventCards(allPartying, "Partying Events - " + type);
                } else {
                    displayEventCards(filtered, "Partying Events - " + type);
                }
            });
            eventCardsFlowPane.getChildren().add(typeCard);
        }
        
        showUserBrowse();
    }
    
    private VBox createPartyTypeCard(String icon, String partyType) {
        VBox card = new VBox();
        card.setPrefWidth(200);
        card.setPrefHeight(100);
        card.setStyle(
            "-fx-background-color: #fce7f3; " +
            "-fx-border-color: #db2777; " +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 15;" +
            "-fx-cursor: hand;"
        );

        Label header = new Label(icon + "\n" + partyType);
        header.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937; " +
            "-fx-text-alignment: center;"
        );
        header.setAlignment(Pos.CENTER);
        header.setTextAlignment(TextAlignment.CENTER);
        header.setWrapText(true);

        card.getChildren().add(header);
        return card;
    }

    @FXML
    private void showPaddleEvents() {
        // Show Paddle options: Events (tournaments) or Private Court booking
        displayPaddleOptions();
    }
    
    private void displayPaddleOptions() {
        categoryTitleLabel.setText("Paddle - Choisir une option");
        categoryEventCountLabel.setText("Sélectionnez comment souhaitez-vous réserver");
        eventCardsFlowPane.getChildren().clear();

        // Option 1: Events (Tournaments)
        VBox eventsCard = createPaddleOptionCard(
            "🏆", 
            "Événements", 
            "Participez à des tournois et entraînements",
            "-fx-background-color: #fef3c7; -fx-border-color: #f59e0b;"
        );
        eventsCard.setOnMouseClicked(e -> {
            List<EventInstance> filtered = filterByType("Paddle");
            displayEventCards(filtered, "Paddle Événements - Tournois et Entraînements");
        });

        // Option 2: Private Court
        VBox courtCard = createPaddleOptionCard(
            "🏸", 
            "Court Privé", 
            "Réservez un court pour votre groupe",
            "-fx-background-color: #e0f2fe; -fx-border-color: #0284c7;"
        );
        courtCard.setOnMouseClicked(e -> {
            showPaddleCourts();
        });

        eventCardsFlowPane.getChildren().addAll(eventsCard, courtCard);
        showUserBrowse();
    }
    
    private VBox createPaddleOptionCard(String icon, String title, String description, String style) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(200);
        card.setStyle(
            style +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;"
        );

        // Header with icon
        Label header = new Label(icon + " " + title);
        header.setStyle(
            "-fx-font-size: 20; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937; " +
            "-fx-padding: 30;"
        );
        header.setAlignment(Pos.CENTER);
        header.setTextAlignment(TextAlignment.CENTER);

        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 14; " +
            "-fx-text-fill: #4b5563; " +
            "-fx-padding: 0 20 20 20;"
        );
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(header, descLabel);
        
        // Hover effect
        card.setOnMouseEntered(evt -> {
            card.setStyle(
                style +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 3;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
            );
        });
        
        card.setOnMouseExited(evt -> {
            card.setStyle(
                style +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;" +
                "-fx-cursor: hand;"
            );
        });

        return card;
    }
    
    @FXML
    private void showPaddleCourts() {
        // Show paddle courts grouped by venue for private booking
        List<Court> courts = getCourtsForDisplay();
        // Group courts by venue
        Map<String, List<Court>> courtsByVenue = courts.stream()
            .collect(Collectors.groupingBy(Court::getVenueName));
        
        displayVenueCourtCards(courtsByVenue, "Paddle - Réserver un Court Privé");
    }
    
    private void displayVenueCourtCards(Map<String, List<Court>> courtsByVenue, String title) {
        categoryTitleLabel.setText(title);
        int totalCourts = courtsByVenue.values().stream().mapToInt(List::size).sum();
        categoryEventCountLabel.setText(totalCourts + " courts available at " + courtsByVenue.size() + " locations");
        eventCardsFlowPane.getChildren().clear();

        if (courtsByVenue.isEmpty()) {
            Label noEventsLabel = new Label("No paddle courts available.\nCheck back later!");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #64748b; -fx-padding: 40;");
            noEventsLabel.setTextAlignment(TextAlignment.CENTER);
            eventCardsFlowPane.getChildren().add(noEventsLabel);
            return;
        }

        for (Map.Entry<String, List<Court>> entry : courtsByVenue.entrySet()) {
            VBox card = createVenueCourtCard(entry.getKey(), entry.getValue());
            eventCardsFlowPane.getChildren().add(card);
        }

        // Switch to Browse by Type tab
        showUserBrowse();
    }
    
    private VBox createVenueCourtCard(String venueName, List<Court> courts) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(280);
        card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
        );

        // Header - Venue name
        Label header = new Label("🏸 " + venueName);
        header.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #0284c7; " +
            "-fx-background-radius: 15 15 0 0;"
        );

        // Content
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 15;");

        Label courtsLabel = new Label("📋 " + courts.size() + " court(s)");
        courtsLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #0369a1;");

        // Count indoor vs outdoor
        long indoorCount = courts.stream().filter(Court::getIsIndoor).count();
        long outdoorCount = courts.size() - indoorCount;
        String typeInfo = "";
        if (indoorCount > 0) typeInfo += "🏟️ " + indoorCount + " indoor";
        if (outdoorCount > 0) {
            if (!typeInfo.isEmpty()) typeInfo += " | ";
            typeInfo += "☀️ " + outdoorCount + " outdoor";
        }
        Label typeLabel = new Label(typeInfo);
        typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        // Get city from first court
        String city = courts.get(0).getVenueCity();
        Label cityLabel = new Label("🌆 " + (city != null ? city : ""));
        cityLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        // Average price
        double avgPrice = courts.stream().mapToDouble(Court::getPricePerHour).average().orElse(0);
        Label priceLabel = new Label(String.format("💰 %.2f TND/heure (avg)", avgPrice));
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        content.getChildren().addAll(courtsLabel, typeLabel, cityLabel, priceLabel);

        // Reserve button
        Button reserveBtn = new Button("Voir les Créneaux");
        reserveBtn.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );
        
        // Store venue info in the button for later use
        final String venueNameFinal = venueName;
        final List<Court> courtsFinal = courts;
        reserveBtn.setOnAction(e -> {
            // Show court selection for this venue with calendar
            showVenueCourtSelection(venueNameFinal, courtsFinal);
        });

        card.getChildren().addAll(header, content, reserveBtn);
        
        // Hover effect
        card.setOnMouseEntered(evt -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0ea5e9;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);" +
                "-fx-padding: 0;"
            );
        });
        
        card.setOnMouseExited(evt -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
            );
        });

        return card;
    }
    
    private void showVenueCourtSelection(String venueName, List<Court> courts) {
        // Navigate to the paddel reservation details with venue context
        // For now, we'll show individual court cards with calendar
        displayCourtCardsWithCalendar(courts, "Sélectionner un Court - " + venueName);
    }
    
    private void displayCourtCardsWithCalendar(List<Court> courts, String title) {
        categoryTitleLabel.setText(title);
        categoryEventCountLabel.setText(courts.size() + " courts available");
        eventCardsFlowPane.getChildren().clear();

        if (courts.isEmpty()) {
            Label noEventsLabel = new Label("No courts available.");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #64748b; -fx-padding: 40;");
            noEventsLabel.setTextAlignment(TextAlignment.CENTER);
            eventCardsFlowPane.getChildren().add(noEventsLabel);
            return;
        }

        for (Court court : courts) {
            VBox card = createCourtCardWithCalendar(court);
            eventCardsFlowPane.getChildren().add(card);
        }

        showUserBrowse();
    }
    
    private VBox createCourtCardWithCalendar(Court court) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(320);
        card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
        );

        // Header - Court name and type
        String typeIcon = court.getIsIndoor() ? "🏟️" : "☀️";
        String courtType = court.getIsIndoor() ? "Interior" : "Extérieur";
        Label header = new Label("🏸 " + court.getName());
        header.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #0284c7; " +
            "-fx-background-radius: 15 15 0 0;"
        );

        // Content
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 15;");

        Label typeLabel = new Label(typeIcon + " " + courtType);
        typeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #0369a1;");

        Label venueLabel = new Label("📍 " + court.getVenueName());
        venueLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        Label cityLabel = new Label("🌆 " + (court.getVenueCity() != null ? court.getVenueCity() : ""));
        cityLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        Label priceLabel = new Label(String.format("💰 %.2f TND/heure", court.getPricePerHour()));
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        content.getChildren().addAll(typeLabel, venueLabel, cityLabel, priceLabel);

        // Reserve button
        Button reserveBtn = new Button("Réserver");
        reserveBtn.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );
        reserveBtn.setOnAction(e -> {
            EventContext.setSelectedCourtId(court.getId());
            EventContext.setPreviousPage("courts"); // Store that we came from courts view
            try {
                Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Réserver le Court");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(header, content, reserveBtn);
        
        // Hover effect
        card.setOnMouseEntered(evt -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0ea5e9;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);" +
                "-fx-padding: 0;"
            );
        });
        
        card.setOnMouseExited(evt -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
            );
        });

        return card;
    }
    
    private List<Court> getCourtsForDisplay() {
        List<Court> courts = new ArrayList<>();
        try {
            VenueService venueService = new VenueService();
            courts = venueService.getPaddleCourts();
        } catch (Exception e) {
            System.err.println("Error loading courts: " + e.getMessage());
        }
        return courts;
    }
    
    private void displayCourtCards(List<Court> courts, String title) {
        categoryTitleLabel.setText(title);
        categoryEventCountLabel.setText(courts.size() + " courts available");
        eventCardsFlowPane.getChildren().clear();

        if (courts.isEmpty()) {
            Label noEventsLabel = new Label("No paddle courts available.\nCheck back later!");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #64748b; -fx-padding: 40;");
            noEventsLabel.setTextAlignment(TextAlignment.CENTER);
            eventCardsFlowPane.getChildren().add(noEventsLabel);
            return;
        }

        for (Court court : courts) {
            VBox card = createCourtCard(court);
            eventCardsFlowPane.getChildren().add(card);
        }

        // Switch to Browse by Type tab
        showUserBrowse();
    }
    
    private VBox createCourtCard(Court court) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(280);
        card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
        );

        // Header - Court name and type
        String typeIcon = court.getIsIndoor() ? "🏟️" : "☀️";
        String courtType = court.getIsIndoor() ? "Interior" : "Extérieur";
        Label header = new Label("🏸 " + court.getName());
        header.setStyle(
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #0284c7; " +
            "-fx-background-radius: 15 15 0 0;"
        );

        // Content
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 15;");

        Label typeLabel = new Label(typeIcon + " " + courtType);
        typeLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #0369a1;");

        Label venueLabel = new Label("📍 " + court.getVenueName());
        venueLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        Label cityLabel = new Label("🌆 " + (court.getVenueCity() != null ? court.getVenueCity() : ""));
        cityLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #475569;");

        Label priceLabel = new Label(String.format("💰 %.2f TND/heure", court.getPricePerHour()));
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #dc2626;");

        content.getChildren().addAll(typeLabel, venueLabel, cityLabel, priceLabel);

        // Reserve button
        Button reserveBtn = new Button("Réserver");
        reserveBtn.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );
        reserveBtn.setOnAction(e -> {
            com.synapseevent.utils.EventContext.setSelectedCourtId(court.getId());
            try {
                Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Réserver le Court");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(header, content, reserveBtn);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0ea5e9;" +
                "-fx-border-width: 2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);" +
                "-fx-padding: 0;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: #e0f2fe;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #0284c7;" +
                "-fx-border-width: 1.5;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
                "-fx-padding: 0;"
            );
        });

        return card;
    }

    @FXML
    private void showTeamBuildingEvents() {
        // Show team building options: existing events OR custom request
        displayTeamBuildingOptions();
    }
    
    private void displayTeamBuildingOptions() {
        categoryTitleLabel.setText("Team Building - Choose an option");
        categoryEventCountLabel.setText("Select how you want to book team building");
        eventCardsFlowPane.getChildren().clear();

        // Option 1: Browse Existing Events (Packs)
        VBox eventsCard = createFormationOptionCard(
            "🎯", 
            "Browse Existing Packs", 
            "Choose from pre-made team building packages",
            "-fx-background-color: #dbeafe; -fx-border-color: #2563eb;"
        );
        eventsCard.setOnMouseClicked(e -> {
            List<EventInstance> filtered = filterByType("TeamBuilding");
            displayEventCards(filtered, "Team Building - Available Packs");
        });

        // Option 2: Request Custom Team Building
        VBox customCard = createFormationOptionCard(
            "✨", 
            "Request Custom Team Building", 
            "Create your own team building with selected activities",
            "-fx-background-color: #fef3c7; -fx-border-color: #d97706;"
        );
        customCard.setOnMouseClicked(e -> {
            showCustomTeamBuildingRequest();
        });

        eventCardsFlowPane.getChildren().addAll(eventsCard, customCard);
        showUserBrowse();
    }
    
    private void showCustomTeamBuildingRequest() {
        // Show custom team building request form
        categoryTitleLabel.setText("Request Custom Team Building");
        categoryEventCountLabel.setText("Fill in the form to request a custom team building");
        eventCardsFlowPane.getChildren().clear();
        
        // Create a form VBox for custom request
        VBox formBox = new VBox();
        formBox.setPrefWidth(500);
        formBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14; " +
            "-fx-padding: 30;"
        );
        
        Label titleLabel = new Label("Custom Team Building Request");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        
        // Venue selection
        Label venueLabel = new Label("Select Venue:");
        venueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 10 0 5 0;");
        ComboBox<Venue> venueCombo = new ComboBox<>();
        try {
            venueCombo.setItems(FXCollections.observableArrayList(new VenueService().readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        venueCombo.setPrefWidth(400);
        
        // Date selection
        Label dateLabel = new Label("Select Date:");
        dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 10 0 5 0;");
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(400);
        
        // Capacity
        Label capacityLabel = new Label("Number of Participants:");
        capacityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 10 0 5 0;");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Enter number of participants");
        capacityField.setPrefWidth(400);
        
        // Activities selection - using CheckBoxes for easy selection
        Label activitiesLabel = new Label("Select Activities:");
        activitiesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 10 0 5 0;");
        
        VBox activitiesBox = new VBox(8);
        activitiesBox.setStyle("-fx-padding: 5; -fx-background-color: #f9fafb; -fx-border-color: #d1d5db; -fx-border-radius: 6;");
        List<CheckBox> activityCheckBoxes = new ArrayList<>();
        
        try {
            TeamBuildingActivityService activityService = new TeamBuildingActivityService();
            List<TeamBuildingActivity> allActivities = activityService.readAll();
            for (TeamBuildingActivity activity : allActivities) {
                CheckBox cb = new CheckBox(activity.getName() + " - " + 
                    String.format("%.2f", activity.getPricePerPerson()) + " TND/person (" + 
                    activity.getCategory() + ")");
                cb.setUserData(activity);
                cb.setStyle("-fx-padding: 5; -fx-text-fill: #374151;");
                activityCheckBoxes.add(cb);
                activitiesBox.getChildren().add(cb);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("No activities available. Please restart the app.");
            errorLabel.setStyle("-fx-text-fill: red;");
            activitiesBox.getChildren().add(errorLabel);
        }
        
        ScrollPane activitiesScroll = new ScrollPane(activitiesBox);
        activitiesScroll.setPrefHeight(150);
        activitiesScroll.setStyle("-fx-background: transparent;");
        
        // Selected activities display with prices
        Label selectedActivitiesLabel = new Label("Selected Activities:");
        selectedActivitiesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151; -fx-padding: 10 0 5 0;");
        TextArea selectedActivitiesArea = new TextArea();
        selectedActivitiesArea.setEditable(false);
        selectedActivitiesArea.setPrefHeight(100);
        selectedActivitiesArea.setStyle("-fx-background-color: #f3f4f6; -fx-border-color: #d1d5db; -fx-border-radius: 6;");
        selectedActivitiesArea.setText("No activities selected yet. Click 'Calculate Price' to see your selection.");
        
        // Price display
        Label priceLabel = new Label("Estimated Price: 0 TND");
        priceLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #059669; -fx-padding: 15 0 5 0;");
        
        // Calculate price button
        Button calculateBtn = new Button("Calculate Price");
        calculateBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-padding: 10 20; -fx-font-weight: bold;");
        calculateBtn.setOnAction(e -> {
            // Get selected activities from checkboxes
            List<TeamBuildingActivity> selectedItems = new ArrayList<>();
            for (CheckBox cb : activityCheckBoxes) {
                if (cb.isSelected()) {
                    selectedItems.add((TeamBuildingActivity) cb.getUserData());
                }
            }
            
            // Update selected activities display
            StringBuilder selectedText = new StringBuilder();
            double activitiesTotal = 0;
            double venueCost = 0;
            int participants = 10;
            try {
                String capacityText = capacityField.getText();
                if (capacityText != null && !capacityText.trim().isEmpty()) {
                    participants = Integer.parseInt(capacityText.trim());
                }
            } catch (NumberFormatException ex) {
                participants = 10;
            }
            
            // Calculate venue cost
            Venue selectedVenue = venueCombo.getValue();
            if (selectedVenue != null && selectedVenue.getType() != null) {
                switch (selectedVenue.getType().toUpperCase()) {
                    case "HOTEL":
                    case "RESORT":
                        venueCost = 500;
                        break;
                    case "RESTAURANT":
                        venueCost = 300;
                        break;
                    default:
                        venueCost = 200;
                }
            }
            
            // Build price breakdown
            selectedText.append("===== PRICE BREAKDOWN =====\n\n");
            
            // Venue section
            selectedText.append("📍 VENUE:\n");
            selectedText.append("-".repeat(30)).append("\n");
            if (selectedVenue != null) {
                selectedText.append(String.format("  Venue: %s\n", selectedVenue.getName()));
                selectedText.append(String.format("  Type: %s\n", selectedVenue.getType()));
            } else {
                selectedText.append("  No venue selected\n");
            }
            selectedText.append(String.format("  Venue Cost: %.2f TND\n\n", venueCost));
            
            // Activities section
            selectedText.append("🎯 ACTIVITIES:\n");
            selectedText.append("-".repeat(30)).append("\n");
            
            if (selectedItems.isEmpty()) {
                selectedText.append("  No activities selected.\n");
            } else {
                for (TeamBuildingActivity activity : selectedItems) {
                    double activityPrice = activity.getPricePerPerson() != null ? activity.getPricePerPerson() : 0;
                    double totalActivityPrice = activityPrice * participants;
                    selectedText.append(String.format("  • %s\n", activity.getName()));
                    selectedText.append(String.format("    Price/person: %.2f TND\n", activityPrice));
                    selectedText.append(String.format("    Total (%d persons): %.2f TND\n", participants, totalActivityPrice));
                    activitiesTotal += totalActivityPrice;
                }
            }
            selectedText.append(String.format("\n  Activities Subtotal: %.2f TND\n\n", activitiesTotal));
            
            // Total
            double totalPrice = venueCost + activitiesTotal;
            selectedText.append("=".repeat(30)).append("\n");
            selectedText.append(String.format("TOTAL ESTIMATED PRICE: %.2f TND\n", totalPrice));
            selectedText.append(String.format("(Venue: %.2f + Activities: %.2f)\n", venueCost, activitiesTotal));
            
            selectedActivitiesArea.setText(selectedText.toString());
            
            // Also update the price label
            priceLabel.setText("Estimated Price: " + String.format("%.2f", totalPrice) + " TND");
        });
        
        // Submit button
        Button submitBtn = new Button("Submit Request");
        submitBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-padding: 12 24; -fx-font-weight: bold; -fx-margin: 20 0 0 0;");
        submitBtn.setOnAction(e -> {
            // Get capacity from text field
            int capacity = 10;
            try {
                String capacityText = capacityField.getText();
                if (capacityText != null && !capacityText.trim().isEmpty()) {
                    capacity = Integer.parseInt(capacityText.trim());
                }
            } catch (NumberFormatException ex) {
                capacity = 10;
            }
            
            // Get selected activities from checkboxes
            List<TeamBuildingActivity> selectedActivities = new ArrayList<>();
            for (CheckBox cb : activityCheckBoxes) {
                if (cb.isSelected()) {
                    selectedActivities.add((TeamBuildingActivity) cb.getUserData());
                }
            }
            
            // Save the custom request
            saveCustomTeamBuildingRequest(
                venueCombo.getValue() != null ? venueCombo.getValue().getName() : "",
                datePicker.getValue(),
                capacity,
                selectedActivities,
                venueCombo.getValue(),
                priceLabel.getText()
            );
        });
        
        formBox.getChildren().addAll(titleLabel, venueLabel, venueCombo, dateLabel, datePicker, 
            capacityLabel, capacityField, activitiesLabel, activitiesScroll, selectedActivitiesLabel, 
            selectedActivitiesArea, calculateBtn, priceLabel, submitBtn);
        eventCardsFlowPane.getChildren().add(formBox);
        showUserBrowse();
    }
    
    private double calculateUserPrice(Venue venue, Integer capacity, List<TeamBuildingActivity> activities) {
        double basePrice = 0;
        
        // Venue cost
        if (venue != null && venue.getType() != null) {
            switch (venue.getType().toUpperCase()) {
                case "HOTEL":
                case "RESORT":
                    basePrice += 500;
                    break;
                case "RESTAURANT":
                    basePrice += 300;
                    break;
                default:
                    basePrice += 200;
            }
        }
        
        // Activity costs per person
        double activityCostPerPerson = 0;
        if (activities != null) {
            for (TeamBuildingActivity activity : activities) {
                if (activity.getPricePerPerson() != null) {
                    activityCostPerPerson += activity.getPricePerPerson();
                }
            }
        }
        
        int participants = capacity != null ? capacity : 10;
        return basePrice + (activityCostPerPerson * participants);
    }
    
    private void saveCustomTeamBuildingRequest(String venue, LocalDate date, Integer capacity, 
        List<TeamBuildingActivity> activities, Venue venueObj, String priceText) {
        // Build activities string
        StringBuilder activitiesStr = new StringBuilder();
        if (activities != null) {
            for (TeamBuildingActivity activity : activities) {
                if (activitiesStr.length() > 0) activitiesStr.append(", ");
                activitiesStr.append(activity.getName());
            }
        }
        
        // Extract price
        double price = 0;
        if (priceText != null && priceText.contains("TND")) {
            try {
                price = Double.parseDouble(priceText.replace("Estimated Price:", "").replace("TND", "").trim());
            } catch (NumberFormatException e) {
                price = 0;
            }
        }
        
        // Get user email
        String userEmail = "user@synapse.com";
        if (CurrentUser.getCurrentUser() != null && CurrentUser.getCurrentUser().getEmail() != null) {
            userEmail = CurrentUser.getCurrentUser().getEmail();
        }
        
        // Save as TeamBuildingEvent
        com.synapseevent.entities.TeamBuildingEvent event = new com.synapseevent.entities.TeamBuildingEvent(
            "Custom Request - " + (venue != null ? venue : "Team Building"),
            date,
            java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(17, 0),
            venue,
            capacity,
            price,
            userEmail,
            "Custom team building request with activities: " + activitiesStr.toString(),
            "pending"
        );
        event.setIsPack(true);
        event.setActivities(activitiesStr.toString());
        
        try {
            com.synapseevent.service.TeamBuildingEventService service = new com.synapseevent.service.TeamBuildingEventService();
            service.ajouter(event);
            
            // Also add to event_instance
            EventInstance ei = new EventInstance(
                event.getName(), date,
                java.time.LocalTime.of(9, 0),
                java.time.LocalTime.of(17, 0),
                venue, capacity, price,
                userEmail,
                "Custom team building request with activities: " + activitiesStr.toString(),
                "pending",
                "TeamBuilding"
            );
            new com.synapseevent.service.EventInstanceService().ajouter(ei);
            
            // Show success message
            categoryTitleLabel.setText("Request Submitted!");
            categoryEventCountLabel.setText("Your custom team building request has been submitted successfully.");
            eventCardsFlowPane.getChildren().clear();
            
            Label successLabel = new Label("✅ Your request has been submitted!\n\nWe will contact you soon with more details.");
            successLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #059669; -fx-padding: 30;");
            eventCardsFlowPane.getChildren().add(successLabel);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showFormationEvents() {
        // Show formation options instead of directly showing events
        displayFormationOptions();
    }
    
    private void displayFormationOptions() {
        categoryTitleLabel.setText("Formation - Choose an option");
        categoryEventCountLabel.setText("Select how you want to browse training sessions");
        eventCardsFlowPane.getChildren().clear();

        // Option 1: See All Events
        VBox eventsCard = createFormationOptionCard(
            "📚", 
            "See All Sessions", 
            "Browse all available training sessions",
            "-fx-background-color: #dbeafe; -fx-border-color: #2563eb;"
        );
        eventsCard.setOnMouseClicked(e -> {
            List<EventInstance> filtered = filterByType("Formation");
            displayEventCards(filtered, "Formation Sessions - All Available");
        });

        // Option 2: Browse by Training Center (Venue)
        VBox venueCard = createFormationOptionCard(
            "🏢", 
            "Browse by Training Center", 
            "Find training sessions at specific centers",
            "-fx-background-color: #d1fae5; -fx-border-color: #059669;"
        );
        venueCard.setOnMouseClicked(e -> {
            showFormationByVenue();
        });

        // Option 3: Browse by Trainer
        VBox trainerCard = createFormationOptionCard(
            "👨‍🏫", 
            "Browse by Trainer", 
            "Find trainers and their sessions",
            "-fx-background-color: #fef3c7; -fx-border-color: #d97706;"
        );
        trainerCard.setOnMouseClicked(e -> {
            showFormationByTrainer();
        });

        eventCardsFlowPane.getChildren().addAll(eventsCard, venueCard, trainerCard);
        showUserBrowse();
    }
    
    private VBox createFormationOptionCard(String icon, String title, String description, String style) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(180);
        card.setStyle(
            style +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 0;" +
            "-fx-cursor: hand;"
        );

        // Header with icon
        Label header = new Label(icon + " " + title);
        header.setStyle(
            "-fx-font-size: 18; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937; " +
            "-fx-padding: 25;"
        );
        header.setAlignment(Pos.CENTER);
        header.setTextAlignment(TextAlignment.CENTER);

        // Description
        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-text-fill: #4b5563; " +
            "-fx-padding: 0 20 20 20;"
        );
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setWrapText(true);

        card.getChildren().addAll(header, descLabel);
        return card;
    }
    
    private void showFormationByVenue() {
        // Filter formation events by venue/training center
        List<EventInstance> formationEvents = filterByType("Formation");
        
        // Group by venue (location)
        Map<String, List<EventInstance>> eventsByVenue = formationEvents.stream()
            .collect(Collectors.groupingBy(EventInstance::getLocation));
        
        categoryTitleLabel.setText("Formation - Browse by Training Center");
        categoryEventCountLabel.setText("Select a training center to see sessions");
        eventCardsFlowPane.getChildren().clear();

        for (Map.Entry<String, List<EventInstance>> entry : eventsByVenue.entrySet()) {
            String venue = entry.getKey();
            List<EventInstance> events = entry.getValue();
            
            VBox venueCard = createTrainingCenterCard(venue, events.size());
            final String venueName = venue;
            venueCard.setOnMouseClicked(e -> {
                displayEventCards(events, "Formation at " + venueName);
            });
            eventCardsFlowPane.getChildren().add(venueCard);
        }
        
        if (eventsByVenue.isEmpty()) {
            Label noVenuesLabel = new Label("No training centers found for formation events");
            noVenuesLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280;");
            eventCardsFlowPane.getChildren().add(noVenuesLabel);
        }
        
        showUserBrowse();
    }
    
    private VBox createTrainingCenterCard(String centerName, int eventCount) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(120);
        card.setStyle(
            "-fx-background-color: #d1fae5; " +
            "-fx-border-color: #059669; " +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 20;" +
            "-fx-cursor: hand;"
        );

        Label header = new Label("🏢 " + centerName);
        header.setStyle(
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937;"
        );

        Label countLabel = new Label(eventCount + " sessions available");
        countLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-text-fill: #6b7280; " +
            "-fx-padding: 10 0 0 0;"
        );

        card.getChildren().addAll(header, countLabel);
        return card;
    }
    
    private void showFormationByTrainer() {
        // Browse by trainer (organizer)
        List<EventInstance> formationEvents = filterByType("Formation");
        
        // Group by trainer (organizer)
        Map<String, List<EventInstance>> eventsByTrainer = formationEvents.stream()
            .filter(e -> e.getOrganizer() != null && !e.getOrganizer().isEmpty())
            .collect(Collectors.groupingBy(EventInstance::getOrganizer));
        
        categoryTitleLabel.setText("Formation - Browse by Trainer");
        categoryEventCountLabel.setText("Select a trainer to see their sessions");
        eventCardsFlowPane.getChildren().clear();

        for (Map.Entry<String, List<EventInstance>> entry : eventsByTrainer.entrySet()) {
            String trainer = entry.getKey();
            List<EventInstance> events = entry.getValue();
            
            VBox trainerCard = createTrainerCard(trainer, events.size());
            final String trainerName = trainer;
            trainerCard.setOnMouseClicked(e -> {
                displayEventCards(events, "Formation with " + trainerName);
            });
            eventCardsFlowPane.getChildren().add(trainerCard);
        }
        
        if (eventsByTrainer.isEmpty()) {
            Label noTrainersLabel = new Label("No trainers found for formation events");
            noTrainersLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280;");
            eventCardsFlowPane.getChildren().add(noTrainersLabel);
        }
        
        showUserBrowse();
    }
    
    private VBox createTrainerCard(String trainerName, int eventCount) {
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setPrefHeight(120);
        card.setStyle(
            "-fx-background-color: #fef3c7; " +
            "-fx-border-color: #d97706; " +
            "-fx-background-radius: 14;" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0, 0, 3);" +
            "-fx-padding: 20;" +
            "-fx-cursor: hand;"
        );

        Label header = new Label("👨‍🏫 " + trainerName);
        header.setStyle(
            "-fx-font-size: 16; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #1f2937;"
        );

        Label countLabel = new Label(eventCount + " sessions available");
        countLabel.setStyle(
            "-fx-font-size: 13; " +
            "-fx-text-fill: #6b7280; " +
            "-fx-padding: 10 0 0 0;"
        );

        card.getChildren().addAll(header, countLabel);
        return card;
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
        VBox card = new VBox();
        card.setPrefWidth(260);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.55);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);" +
                        "-fx-padding: 0;"
        );

        // Header bar - blue like table header
        String emoji = getEventEmoji(ei.getType());
        Label header = new Label(emoji + "  " + (ei.getType() != null ? ei.getType() : "Event"));
        header.setMaxWidth(Double.MAX_VALUE);
        header.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-background-radius: 14 14 0 0;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 12 16;"
        );

        // Content area
        VBox content = new VBox(8);
        content.setStyle("-fx-padding: 14 16 16 16;");

        // Event name
        Label nameLabel = new Label(ei.getName() != null ? ei.getName() : "Unnamed Event");
        nameLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #0f172a;"
        );
        nameLabel.setWrapText(true);

        // Date
        Label dateLabel = new Label("📅  " + (ei.getDate() != null ? ei.getDate().toString() : "TBD"));
        dateLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #475569;"
        );

        // Location
        Label locationLabel = new Label("📍  " + (ei.getLocation() != null ? ei.getLocation() : "TBD"));
        locationLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-fill: #475569;"
        );
        locationLabel.setWrapText(true);

        // Price
        Label priceLabel = new Label(ei.getPrice() != null ? ei.getPrice() + " TND" : "Free");
        priceLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-text-fill: #1e40af;"
        );

        // Status badge
        String status = ei.getStatus() != null ? ei.getStatus().toUpperCase() : "AVAILABLE";
        Label statusLabel = new Label(status);
        String badgeColor = "published".equalsIgnoreCase(ei.getStatus()) ? "#dcfce7" :
                "pending".equalsIgnoreCase(ei.getStatus())   ? "#fef9c3" : "#dbeafe";
        String badgeText  = "published".equalsIgnoreCase(ei.getStatus()) ? "#166534" :
                "pending".equalsIgnoreCase(ei.getStatus())   ? "#854d0e" : "#1e40af";
        statusLabel.setStyle(
                "-fx-background-color: " + badgeColor + ";" +
                        "-fx-text-fill: " + badgeText + ";" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 999;"
        );

        // Book button
        Button bookBtn = new Button("Book Now");
        bookBtn.setMaxWidth(Double.MAX_VALUE);
        bookBtn.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.30), 8, 0, 0, 3);"
        );
        bookBtn.setOnAction(e -> showEventDetailsDialog(ei));

        bookBtn.setOnMouseEntered(e -> bookBtn.setStyle(
                "-fx-background-color: #2563eb;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;"
        ));
        bookBtn.setOnMouseExited(e -> bookBtn.setStyle(
                "-fx-background-color: #3b82f6;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-padding: 10 20;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: transparent;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.30), 8, 0, 0, 3);"
        ));

        content.getChildren().addAll(nameLabel, dateLabel, locationLabel, priceLabel, statusLabel, bookBtn);
        card.getChildren().addAll(header, content);

        // Hover effect on card
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.85);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: #3b82f6;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.18), 14, 0, 0, 4);" +
                        "-fx-padding: 0;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.55);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 3);" +
                        "-fx-padding: 0;"
        ));

        return card;
    }

    private String getCardColor(String type) {
        if (type == null) return "#A77693";
        switch (type) {
            case "Partying":     return "#A77693";
            case "Paddle":       return "#174871";
            case "TeamBuilding": return "#0F2D4D";
            case "Formation":    return "#6c4a10";
            case "Anniversary":  return "#8a3a00";
            default:             return "#174871";
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
            private final HBox buttonsBox = new HBox(5);
            private final Button reviewBtn = new Button("Review");
            {
                reviewBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;");
                
                reviewBtn.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    openReviewDialog(booking);
                });
                
                buttonsBox.getChildren().add(reviewBtn);
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
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
        cityFilterComboBox.getItems().addAll(venueService.getAllCities());
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
        venueComboBox.setItems(FXCollections.observableArrayList(venueService.findByTypeAndCity(type, city)));
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
    
    private void cancelBooking(Booking booking) {
        if (booking == null) return;
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Are you sure you want to cancel this booking?");
        confirmAlert.setContentText("This action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = bookingService.cancelBooking(booking.getId());
                    if (success) {
                        showAlert("Success", "Booking cancelled successfully.");
                        // Refresh the bookings table
                        loadBookings();
                    } else {
                        showAlert("Error", "Failed to cancel booking.");
                    }
                } catch (SQLException e) {
                    showAlert("Error", "Error cancelling booking: " + e.getMessage());
                }
            }
        });
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

        // Update top title
        if (userTopTitle != null) {
            if (active == userNavHome)      userTopTitle.setText("Home");
            else if (active == userNavBrowse)    userTopTitle.setText("Browse By Type");
            else if (active == userNavBookings)  userTopTitle.setText("My Booking");
            else if (active == userNavRequests)  userTopTitle.setText("Custom Requests");
            else if (active == userNavProfile)   userTopTitle.setText("Profile");
        }
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

    private void setActivePage(Button activeBtn, String title) {
        // Remove active style from all nav buttons
        userNavHome.getStyleClass().remove("user-nav-btn-active");
        userNavBrowse.getStyleClass().remove("user-nav-btn-active");
        userNavBookings.getStyleClass().remove("user-nav-btn-active");
        userNavRequests.getStyleClass().remove("user-nav-btn-active");
        userNavProfile.getStyleClass().remove("user-nav-btn-active");

        // Add active style to clicked button
        activeBtn.getStyleClass().add("user-nav-btn-active");

        // Update top title
        userTopTitle.setText(title);
    }
}

