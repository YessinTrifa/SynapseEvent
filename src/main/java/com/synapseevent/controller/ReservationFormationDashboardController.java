package com.synapseevent.controller;

import com.synapseevent.service.FormationEventService;
import com.synapseevent.entities.FormationEvent;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationFormationDashboardController {
    
    @FXML private TilePane eventsTilePane;
    @FXML private Button refreshBtn;
    
    private final FormationEventService formationEventService = new FormationEventService();
    
    @FXML
    public void initialize() {
        loadEvents();
    }
    
    @FXML
    private void refreshEvents() {
        loadEvents();
    }
    
    private void loadEvents() {
        eventsTilePane.getChildren().clear();
        
        List<FormationEvent> events;
        try {
            events = formationEventService.getPublishedEvents();
        } catch (SQLException e) {
            e.printStackTrace();
            events = new ArrayList<>();
        }
        System.out.println("DEBUG: Found " + events.size() + " formation events");
        
        for (FormationEvent event : events) {
            System.out.println("DEBUG: Loading event: " + event.getName() + " (ID: " + event.getId() + ")");
            VBox eventCard = createEventCard(event);
            eventsTilePane.getChildren().add(eventCard);
        }
        
        if (events.isEmpty()) {
            Label noEventsLabel = new Label("Aucun événement Formation disponible");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            eventsTilePane.getChildren().add(noEventsLabel);
        }
    }
    
    private VBox createEventCard(FormationEvent event) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e5e7eb;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
            "-fx-cursor: hand;"
        );
        
        // Title
        Label titleLabel = new Label(event.getName());
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-wrap-text: true;");
        titleLabel.setPrefWidth(320);
        
        // Date/Time
        String dateTimeStr = "";
        if (event.getDate() != null) {
            dateTimeStr = event.getDate().toString();
            if (event.getStartTime() != null) {
                dateTimeStr += " " + event.getStartTime().toString();
                if (event.getEndTime() != null) {
                    dateTimeStr += " - " + event.getEndTime().toString();
                }
            }
        }
        Label dateTimeLabel = new Label(dateTimeStr);
        dateTimeLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Location
        Label locationLabel = new Label("📍 " + (event.getLocation() != null ? event.getLocation() : ""));
        locationLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Available seats
        Label seatsLabel = new Label("🪑 " + (event.getCapacity() != null ? event.getCapacity() : 0) + " places");
        seatsLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        // Price
        Label priceLabel = new Label("💰 " + (event.getPrice() != null ? String.format("%.2f TND", event.getPrice()) : "N/A"));
        priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        
        // Formation type indicator
        Label typeLabel = new Label("📚 Formation professionnelle");
        typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7c3aed; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, dateTimeLabel, locationLabel, seatsLabel, priceLabel, typeLabel);
        
        // Click handler
        final FormationEvent selectedEvent = event;
        card.setOnMouseClicked(e -> {
            System.out.println("DEBUG: Clicked on event with ID: " + selectedEvent.getId());
            EventContext.setSelectedEventId(selectedEvent.getId());
            System.out.println("DEBUG: EventContext.setSelectedEventId(" + selectedEvent.getId() + ")");
            Navigator.get().go("/fxml/reservationFormationDetails.fxml", "Détails Réservation Formation");
        });
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #7c3aed;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 4);" +
            "-fx-cursor: hand;"
        ));
        
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e5e7eb;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
            "-fx-cursor: hand;"
        ));
        
        return card;
    }
}
