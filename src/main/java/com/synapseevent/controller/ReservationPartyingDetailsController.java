package com.synapseevent.controller;

import com.synapseevent.entities.Booking;
import com.synapseevent.entities.PartyingEvent;
import com.synapseevent.service.BookingService;
import com.synapseevent.service.PartyingEventService;
import java.sql.SQLException;
import java.time.LocalDate;
import com.synapseevent.utils.CurrentUser;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;

public class ReservationPartyingDetailsController {
    
    @FXML private Button backBtn;
    @FXML private Label titleLabel;
    @FXML private Label eventNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label locationLabel;
    @FXML private Label addressLabel;
    @FXML private Label priceLabel;
    @FXML private Label capacityLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label organizerLabel;
    @FXML private Button reserveBtn;

    private final PartyingEventService partyingEventService = new PartyingEventService();
    private final BookingService bookingService = new BookingService();
    private PartyingEvent currentEvent;
    
    @FXML
    public void initialize() {
        Long eventId = EventContext.getSelectedEventId();
        System.out.println("DEBUG: EventContext.getSelectedEventId() = " + eventId);
        
        if (eventId != null) {
            loadEventDetails(eventId);
        } else {
            System.err.println("DEBUG: No event ID found in EventContext");
            showError("Aucun événement sélectionné");
        }
    }
    
    private void loadEventDetails(Long eventId) {
        try {
            currentEvent = partyingEventService.findbyId(eventId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'événement");
            return;
        }
        
        if (currentEvent == null) {
            showError("Party non trouvé");
            return;
        }
        
        // Update UI
        eventNameLabel.setText(currentEvent.getName());
        statusLabel.setText("✅ " + currentEvent.getStatus());
        dateTimeLabel.setText(String.format("%s de %s à %s", 
            currentEvent.getDate().toString(),
            currentEvent.getStartTime().toString(),
            currentEvent.getEndTime().toString()));
        locationLabel.setText(currentEvent.getLocation());
        addressLabel.setText("Adresse non specifiee");
        priceLabel.setText(String.format("%.2f TND", currentEvent.getPrice()));
        capacityLabel.setText(String.format("%d places disponibles", currentEvent.getCapacity()));
        descriptionArea.setText(currentEvent.getDescription() != null ? currentEvent.getDescription() : "Aucune description");
        organizerLabel.setText("Organisateur: " + (currentEvent.getOrganizer() != null ? currentEvent.getOrganizer() : "Non spécifié"));
        
        // Disable reserve button if no seats available
        if (currentEvent.getCapacity() <= 0) {
            reserveBtn.setDisable(true);
            reserveBtn.setText("Complet");
            reserveBtn.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-padding: 12 24; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 14;");
        }
    }
    
    @FXML
    private void goBack() {
        Navigator.get().go("/fxml/reservationPartyingDashboard.fxml", "Réservation Party");
    }
    
    @FXML
    private void showReservationDialog() {
        if (currentEvent == null) return;
        
        // Create reservation dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Réservation - " + currentEvent.getName());
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
                makeReservation(seats);
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void makeReservation(int seats) {
        // Get current user ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            showError("Utilisateur non connecté");
            return;
        }
        
        // Make reservation
        try {
            Booking booking = new Booking(userId, "PARTYING", currentEvent.getId(), LocalDate.now(), "confirmed");
            boolean success = bookingService.ajouter(booking);
            
            if (success) {
                showSuccess(String.format("Réservation réussie ! %d place(s) réservée(s)", seats));
                // Refresh event details
                loadEventDetails(currentEvent.getId());
            } else {
                showError("Erreur lors de la réservation. Vérifiez la disponibilité.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Erreur lors de la reservation.");
        }
    }
    
    private Long getCurrentUserId() {
        // TODO: Récupérer l'ID de l'utilisateur connecté depuis CurrentUser
        // Pour l'instant, retourne 1 comme demandé
        if (CurrentUser.getCurrentUser() != null) {
            return CurrentUser.getCurrentUser().getId();
        }
        return 1L; // TODO: Remplacer par la vraie logique
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
