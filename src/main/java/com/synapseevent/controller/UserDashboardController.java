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
    
    // Profile Tab Fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> preferredCategoriesCombo;
    @FXML private ComboBox<String> preferredLocationsCombo;
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

    // For booking confirmation
    private EventInstance selectedEventForBooking;

    private EventInstanceService eventInstanceService = new EventInstanceService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();
    private VenueService venueService = new VenueService();
    private UserService userService = new UserService();
    private UserPreferencesService userPreferencesService = new UserPreferencesService();

    private Map<String, List<EventInstance>> eventsByType;
    private List<EventInstance> allPublishedEvents = new ArrayList<>();

    @FXML
    public void initialize() {
        // Setup location filter combo with venues
        setupLocationFilter();

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
        
        // Setup profile tab
        setupProfileTab();

        // Load data
        loadEvents();

        loadBookings();
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
        preferredCategoriesCombo.getItems().addAll("All", "Anniversary", "Formation", "Paddle", "Partying", "TeamBuilding");
        preferredCategoriesCombo.setValue("All");
        
        preferredLocationsCombo.getItems().addAll("All Cities", "Tunis", "Sfax", "Sousse", "Kairouan", "Bizerte");
        preferredLocationsCombo.setValue("All Cities");
        
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
                    preferredCategoriesCombo.setValue(prefs.getPreferredCategories());
                }
                if (prefs.getPreferredLocations() != null) {
                    preferredLocationsCombo.setValue(prefs.getPreferredLocations());
                }
                if (prefs.getMaxPrice() != null) {
                    maxPriceField.setText(prefs.getMaxPrice().toString());
                }
                if (prefs.getMinRating() != null) {
                    minRatingCombo.setValue(prefs.getMinRating());
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
            
            String categories = preferredCategoriesCombo.getValue();
            prefs.setPreferredCategories(categories != null && !categories.equals("All") ? categories : null);
            
            String locations = preferredLocationsCombo.getValue();
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

    //private void setupRecommendedEventsTable() {
       // recommendedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
       // recommendedTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
       // recommendedDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
       // recommendedLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
       // recommendedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Custom match percentage column
        //recommendedMatchColumn.setCellFactory(param -> new TableCell<EventInstance, String>() {
           // @Override
            //protected void updateItem(String item, boolean empty) {
             //   super.updateItem(item, empty);
             //   if (empty) {
             //       setGraphic(null);
             //   } else {
               //     EventInstance ei = getTableView().getItems().get(getIndex());
               //     int match = calculateMatchPercentage(ei);
                //    Label matchLabel = new Label(match + "%");
                //    matchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (match >= 80 ? "#10b981" : match >= 60 ? "#f59e0b" : "#6b7280"));
                 //   setGraphic(matchLabel);
               // }
          //  }
       // });

        //recommendedActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            //private final Button bookButton = new Button("Book Now");
            //{
              //  bookButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold;");
             //   bookButton.setOnAction(event -> {
                //    EventInstance ei = getTableView().getItems().get(getIndex());
               //     showEventDetailsDialog(ei);
              //  });
         //   }
          //  @Override
          //  protected void updateItem(Void item, boolean empty) {
              //super.updateItem(item, empty);
               // if (empty) {
                  //  setGraphic(null);
               // } else {
               //     setGraphic(bookButton);
               // }
           // }
        //});
    //}

    private int calculateMatchPercentage(EventInstance ei) {
        int match = 0;
        int factors = 0;
        
        User current = CurrentUser.getCurrentUser();
        if (current == null) return 0;
        
        try {
            UserPreferences prefs = userPreferencesService.findByUserId(current.getId());
            if (prefs != null) {
                // Check category preference
                if (prefs.getPreferredCategories() != null && ei.getType() != null) {
                    factors++;
                    if (ei.getType().equals(prefs.getPreferredCategories())) {
                        match += 40;
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
        categoryTabPane.getSelectionModel().select(0);
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
        
        // Switch to Home tab to show results
        categoryTabPane.getSelectionModel().select(0);
    }

    // Browse by Category Actions
    @FXML
    private void browsePartying() {
        typeFilterCombo.setValue("Partying");
        applyFilters();
        categoryTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void browsePaddle() {
        typeFilterCombo.setValue("Paddle");
        applyFilters();
        categoryTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void browseTeamBuilding() {
        typeFilterCombo.setValue("TeamBuilding");
        applyFilters();
        categoryTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void browseFormation() {
        typeFilterCombo.setValue("Formation");
        applyFilters();
        categoryTabPane.getSelectionModel().select(0);
    }

    @FXML
    private void browseAnniversary() {
        typeFilterCombo.setValue("Anniversary");
        applyFilters();
        categoryTabPane.getSelectionModel().select(0);
    }

    // Category Browse Methods with Visual Cards
    @FXML
    private void showAllCategoryEvents() {
        displayEventCards(allPublishedEvents, "All Events");
    }

    @FXML
    private void showPartyingEvents() {
        List<EventInstance> filtered = filterByType("Partying");
        displayEventCards(filtered, "🎉 Partying Events");
    }

    @FXML
    private void showPaddleEvents() {
        List<EventInstance> filtered = filterByType("Paddle");
        displayEventCards(filtered, "🏄 Paddle Events");
    }

    @FXML
    private void showTeamBuildingEvents() {
        List<EventInstance> filtered = filterByType("TeamBuilding");
        displayEventCards(filtered, "🏢 Team Building Events");
    }

    @FXML
    private void showFormationEvents() {
        List<EventInstance> filtered = filterByType("Formation");
        displayEventCards(filtered, "🎓 Formation Events");
    }

    @FXML
    private void showAnniversaryEvents() {
        List<EventInstance> filtered = filterByType("Anniversary");
        displayEventCards(filtered, "🎂 Anniversary Events");
    }

    private List<EventInstance> filterByType(String type) {
        return allPublishedEvents.stream()
            .filter(e -> e.getType() != null && e.getType().equals(type))
            .filter(e -> e.getDate() != null && !e.getDate().isBefore(LocalDate.now()))
            .sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate()))
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
        categoryTabPane.getSelectionModel().select(1);
    }

    private VBox createEventCard(EventInstance ei) {
        // Determine card color based on event type
        String cardColor = getCardColor(ei.getType());
        String emoji = getEventEmoji(ei.getType());

        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(280);
        card.setStyle(
                "-fx-background-color: rgba(15, 23, 42, 0.85);" +
            "-fx-background-radius: 15; " +
            "-fx-border-radius: 15; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
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
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-padding: 15 15 5 15;");
        nameLabel.setWrapText(true);

        // Date with icon
        Label dateLabel = new Label("📅 " + (ei.getDate() != null ? ei.getDate().toString() : "TBD"));
        dateLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b; -fx-padding: 5 15;");

        // Location with icon
        Label locationLabel = new Label("📍 " + (ei.getLocation() != null ? ei.getLocation() : "TBD"));
        locationLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #64748b; -fx-padding: 5 15;");
        locationLabel.setWrapText(true);

        // Price
        Label priceLabel = new Label("💰 " + (ei.getPrice() != null ? ei.getPrice() + " TND" : "Free"));
        priceLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #10b981; -fx-padding: 10 15;");

        // Status badge
        Label statusLabel = new Label(ei.getStatus() != null ? ei.getStatus().toUpperCase() : "AVAILABLE");
        String statusColor = "available".equalsIgnoreCase(ei.getStatus()) ? "#10b981" : 
                           "pending".equalsIgnoreCase(ei.getStatus()) ? "#f59e0b" : "#6b7280";
        statusLabel.setStyle(
            "-fx-font-size: 11; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: " + statusColor + "; " +
            "-fx-padding: 4 10; " +
            "-fx-background-radius: 10;"
        );
        HBox statusBox = new HBox(statusLabel);
        statusBox.setStyle("-fx-padding: 0 15 10 15;");

        // Book Button
        Button bookBtn = new Button("🎫 Book Now");
        bookBtn.setStyle(
            "-fx-background-color: " + cardColor + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 10;"
        );
        bookBtn.setPrefHeight(40);
        bookBtn.setOnAction(e -> showEventDetailsDialog(ei));

        VBox content = new VBox(nameLabel, dateLabel, locationLabel, priceLabel, statusBox, bookBtn);
        content.setStyle("-fx-padding: 0;");

        card.getChildren().addAll(header, content);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15; " +
                "-fx-border-color: " + cardColor + "; " +
                "-fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 4); " +
                "-fx-padding: 0;"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15; " +
                "-fx-border-color: #e2e8f0; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                "-fx-padding: 0;"
            );
        });

        return card;
    }

    private String getCardColor(String type) {
        if (type == null) return "#6366f1";
        switch (type) {
            case "Partying": return "#ec4899";
            case "Paddle": return "#14b8a6";
            case "TeamBuilding": return "#3b82f6";
            case "Formation": return "#8b5cf6";
            case "Anniversary": return "#f59e0b";
            default: return "#6366f1";
        }
    }

    private String getEventEmoji(String type) {
        if (type == null) return "🎉";
        switch (type) {
            case "Partying": return "🎉";
            case "Paddle": return "🏄";
            case "TeamBuilding": return "🏢";
            case "Formation": return "🎓";
            case "Anniversary": return "🎂";
            default: return "🎉";
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

    @FXML
    private void logout() {
        try {
            CurrentUser.logout();
            Stage stage = (Stage) categoryTabPane.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            stage.setScene(new Scene(root));
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
}
