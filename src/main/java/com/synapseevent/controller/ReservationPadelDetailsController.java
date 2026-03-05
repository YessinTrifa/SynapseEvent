package com.synapseevent.controller;

import com.synapseevent.entities.Court;
import com.synapseevent.entities.Event;
import com.synapseevent.service.PaddleEventService;
import com.synapseevent.service.VenueService;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReservationPadelDetailsController {
    
    @FXML private Button backBtn;
    @FXML private Label titleLabel;
    
    // Court fields
    @FXML private Label courtNameLabel;
    @FXML private Label courtTypeLabel;
    @FXML private Label venueNameLabel;
    @FXML private Label venueAddressLabel;
    @FXML private Label courtTypeDetailLabel;
    @FXML private Label priceLabel;
    @FXML private Label amenitiesLabel;
    @FXML private Label descriptionLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> startTimeComboBox;
    @FXML private ComboBox<String> endTimeComboBox;
    @FXML private Label durationLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button reserveBtn;
    
    @FXML private Label availableSlotsLabel;
    @FXML private VBox availableSlotsContainer;
    
    // Event fields (for event reservations)
    @FXML private Label eventNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label locationLabel;
    @FXML private Label addressLabel;
    @FXML private Label mapLabel;
    
    private final PaddleEventService paddleEventService = new PaddleEventService();
    private final VenueService venueService = new VenueService();
    private Court currentCourt;
    private Event currentEvent;
    
    @FXML
    public void initialize() {
        // Initialize time combo boxes
        initializeTimeComboBoxes();
        
        // Check if we're dealing with a court or an event
        Long courtId = EventContext.getSelectedCourtId();
        Long eventId = EventContext.getSelectedEventId();
        
        if (courtId != null) {
            loadCourtDetails(courtId);
        } else if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            showError("Aucune sélection trouvée");
        }
    }
    
    private void initializeTimeComboBoxes() {
        // Populate time slots from 7:00 to 22:00 with 1h30 increments
        for (int hour = 7; hour <= 22; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                // Skip 22:30 as it would go past closing time
                if (hour == 22 && minute > 0) break;
                String timeStr = String.format("%02d:%02d", hour, minute);
                startTimeComboBox.getItems().add(timeStr);
                endTimeComboBox.getItems().add(timeStr);
            }
        }
        
        // Add listeners for price calculation
        startTimeComboBox.setOnAction(e -> calculateTotalPrice());
        endTimeComboBox.setOnAction(e -> calculateTotalPrice());
    }
    
    private void loadCourtDetails(Long courtId) {
        currentCourt = venueService.getCourtById(courtId);
        
        if (currentCourt == null) {
            showError("Court non trouvé");
            return;
        }
        
        // Update title
        titleLabel.setText("Réserver le Court");
        
        // Court name
        String typeIcon = currentCourt.getIsIndoor() ? "🏟️" : "☀️";
        courtNameLabel.setText(typeIcon + " " + currentCourt.getName());
        
        // Court type
        String courtType = currentCourt.getIsIndoor() ? "Interior" : "Extérieur";
        courtTypeLabel.setText("🏸 " + courtType);
        courtTypeDetailLabel.setText(courtType);
        courtTypeLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        // Venue info
        venueNameLabel.setText(currentCourt.getVenueName());
        venueAddressLabel.setText((currentCourt.getVenueAddress() != null ? currentCourt.getVenueAddress() : "") + 
                                  (currentCourt.getVenueCity() != null ? ", " + currentCourt.getVenueCity() : ""));
        
        // Price
        priceLabel.setText(String.format("%.2f TND/heure", currentCourt.getPricePerHour()));
        
        // Amenities
        amenitiesLabel.setText(currentCourt.getAmenities() != null ? currentCourt.getAmenities() : "Équipements non spécifiés");
        
        // Description
        descriptionLabel.setText(currentCourt.getDescription() != null ? currentCourt.getDescription() : "Aucune description");
        
        // Set date picker to today
        datePicker.setValue(LocalDate.now());
        
        // Add date change listener to show available slots
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && currentCourt != null) {
                loadAvailableTimeSlots(newVal);
            }
            calculateTotalPrice();
        });
        
        // Load initial available slots for today
        loadAvailableTimeSlots(LocalDate.now());
        
        // Initial calculation
        durationLabel.setText("Durée: -");
        totalPriceLabel.setText("Total: - TND");
        
        // Change button text
        reserveBtn.setText("Réserver ce Court");
        reserveBtn.setOnAction(e -> makeCourtReservation());
    }
    
    private void loadAvailableTimeSlots(LocalDate date) {
        if (currentCourt == null || date == null) return;
        
        try {
            // Use 1h30 increments for available slots
            List<String> availableSlots = venueService.getAvailableTimeSlots1h30(
                currentCourt.getId(), 
                Date.valueOf(date)
            );
            
            if (availableSlots.isEmpty()) {
                availableSlotsLabel.setText("Aucun créneau disponible pour cette date");
            } else {
                availableSlotsLabel.setText(String.join(", ", availableSlots));
            }
            availableSlotsContainer.setVisible(true);
        } catch (Exception e) {
            availableSlotsContainer.setVisible(false);
        }
    }
    
    private void loadEventDetails(Long eventId) {
        currentEvent = paddleEventService.findEventById(eventId.intValue());
        
        if (currentEvent == null) {
            showError("Événement non trouvé");
            return;
        }
        
        // Update UI
        eventNameLabel.setText(currentEvent.getName());
        statusLabel.setText("✅ " + currentEvent.getStatus());
        dateTimeLabel.setText(String.format("%s de %s à %s", 
            currentEvent.getDate().toString(),
            currentEvent.getStartTime().toString(),
            currentEvent.getEndTime().toString()));
        locationLabel.setText(String.format("%s, %s", currentEvent.getLocation(), currentEvent.getCity()));
        addressLabel.setText(currentEvent.getAddress() != null ? currentEvent.getAddress() : "Adresse non spécifiée");
        mapLabel.setText(currentEvent.getMap() != null ? "🗺️ " + currentEvent.getMap() : "🗺️ Carte non disponible");
        priceLabel.setText(String.format("%.2f TND", currentEvent.getPrice()));
        amenitiesLabel.setText(currentEvent.getDescription() != null ? currentEvent.getDescription() : "");
        descriptionLabel.setText(currentEvent.getDescription() != null ? currentEvent.getDescription() : "");
        
        // Hide date/time selection for events
        datePicker.setVisible(false);
        startTimeComboBox.setVisible(false);
        endTimeComboBox.setVisible(false);
        durationLabel.setVisible(false);
        totalPriceLabel.setVisible(false);
        
        // Update button for event reservation
        reserveBtn.setText("Réserver");
        reserveBtn.setOnAction(e -> showReservationDialog());
    }
    
    private void calculateTotalPrice() {
        String startTimeStr = startTimeComboBox.getValue();
        String endTimeStr = endTimeComboBox.getValue();
        
        if (startTimeStr != null && endTimeStr != null && currentCourt != null) {
            try {
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);
                
                if (endTime.isAfter(startTime)) {
                    // Calculate duration in half-hours for 1h30 increments
                    long halfHours = java.time.Duration.between(startTime, endTime).toMinutes() / 30;
                    double hours = halfHours * 0.5; // Convert half-hours to hours
                    double total = hours * currentCourt.getPricePerHour();
                    
                    // Format duration display
                    String durationText;
                    if (halfHours == 1) {
                        durationText = "30 min";
                    } else if (halfHours % 2 == 0) {
                        durationText = (halfHours / 2) + "h00";
                    } else {
                        durationText = (halfHours / 2) + "h30";
                    }
                    durationLabel.setText("Durée: " + durationText);
                    totalPriceLabel.setText(String.format("Total: %.2f TND", total));
                } else {
                    durationLabel.setText("Durée: Heures invalides");
                    totalPriceLabel.setText("Total: - TND");
                }
            } catch (Exception e) {
                durationLabel.setText("Durée: -");
                totalPriceLabel.setText("Total: - TND");
            }
        }
    }
    
    @FXML
    private void goBack() {
        // Clear context but remember we came from courts
        EventContext.clear();
        EventContext.setPreviousPage("courts"); // Set after clear to remember where to go back
        
        try {
            Navigator.get().go("/fxml/userDashboard.fxml", "Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void makeCourtReservation() {
        if (currentCourt == null) return;
        
        // Validate inputs
        LocalDate selectedDate = datePicker.getValue();
        String startTimeStr = startTimeComboBox.getValue();
        String endTimeStr = endTimeComboBox.getValue();
        
        if (selectedDate == null) {
            showError("Veuillez sélectionner une date");
            return;
        }
        
        if (startTimeStr == null || endTimeStr == null) {
            showError("Veuillez sélectionner les heures de début et de fin");
            return;
        }
        
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        
        if (!endTime.isAfter(startTime)) {
            showError("L'heure de fin doit être après l'heure de début");
            return;
        }
        
        // Calculate duration in half-hours for price calculation
        long halfHours = java.time.Duration.between(startTime, endTime).toMinutes() / 30;
        double hours = halfHours * 0.5;
        
        // Check availability
        boolean available = venueService.isCourtAvailable(
            currentCourt.getId(),
            Date.valueOf(selectedDate),
            Time.valueOf(startTime),
            Time.valueOf(endTime)
        );
        
        if (!available) {
            showError("Ce créneau horaire n'est pas disponible");
            return;
        }
        
        // Calculate price
        double totalPrice = hours * currentCourt.getPricePerHour();
        
        // Get user ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            showError("Utilisateur non connecté");
            return;
        }
        
        // Make reservation
        boolean success = venueService.createCourtReservation(
            currentCourt.getId(),
            userId,
            Date.valueOf(selectedDate),
            Time.valueOf(startTime),
            Time.valueOf(endTime),
            totalPrice
        );
        
        if (success) {
            showSuccess(String.format("Réservation réussie !\n\nCourt: %s\nDate: %s\nHeure: %s - %s\nTotal: %.2f TND",
                currentCourt.getName(),
                selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                startTimeStr,
                endTimeStr,
                totalPrice));
            
            // Go back to dashboard
            goBack();
        } else {
            showError("Erreur lors de la réservation. Veuillez réessayer.");
        }
    }
    
    @FXML
    private void showReservationDialog() {
        if (currentEvent == null) return;
        
        // Create reservation dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Réserver - " + currentEvent.getName());
        dialog.setHeaderText("Combien de places souhaitez-vous réserver ?");
        
        // Set button types
        ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);
        
        // Create content
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        
        Label infoLabel = new Label(String.format("Places disponibles: %d", currentEvent.getCapacity()));
        infoLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #374151;");
        
        Spinner<Integer> seatsSpinner = new Spinner<>(1, currentEvent.getCapacity(), 1);
        seatsSpinner.setStyle("-fx-font-size: 14;");
        
        Label priceInfoLabel = new Label();
        priceInfoLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Update price when spinner changes
        seatsSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            double totalPrice = newVal * currentEvent.getPrice();
            priceInfoLabel.setText(String.format("Prix total: %.2f TND", totalPrice));
        });
        priceInfoLabel.setText(String.format("Prix total: %.2f TND", currentEvent.getPrice()));
        
        content.getChildren().addAll(infoLabel, new Label("Nombre de places:"), seatsSpinner, priceInfoLabel);
        
        dialog.getDialogPane().setContent(content);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                int seats = seatsSpinner.getValue();
                makeEventReservation(seats);
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void makeEventReservation(int seats) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            showError("Utilisateur non connecté");
            return;
        }
        
        // Make reservation
        boolean success = paddleEventService.reserve(currentEvent.getId().intValue(), userId.intValue(), seats);
        
        if (success) {
            showSuccess(String.format("Réservation réussie ! %d place(s) réservée(s)", seats));
            // Refresh event details
            loadEventDetails(currentEvent.getId());
        } else {
            showError("Erreur lors de la réservation. Vérifiez la disponibilité.");
        }
    }
    
    private Long getCurrentUserId() {
        if (CurrentUser.getCurrentUser() != null) {
            return CurrentUser.getCurrentUser().getId();
        }
        return 1L;
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
