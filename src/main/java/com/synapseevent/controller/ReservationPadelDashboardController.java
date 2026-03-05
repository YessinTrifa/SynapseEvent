package com.synapseevent.controller;

import com.synapseevent.dao.PadelEventDAO;
import com.synapseevent.entities.Event;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class ReservationPadelDashboardController {
    
    @FXML private TilePane eventsTilePane;
    @FXML private Button refreshBtn;
    
    private final PadelEventDAO paddleEventDAO = new PadelEventDAO();
    
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
        
        List<Event> events = paddleEventDAO.findPadelEventsAvailable();
        
        for (Event event : events) {
            VBox eventCard = createEventCard(event);
            eventsTilePane.getChildren().add(eventCard);
        }
        
        if (events.isEmpty()) {
            Label noEventsLabel = new Label("Aucun événement Padel disponible");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            eventsTilePane.getChildren().add(noEventsLabel);
        }
    }
    
    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
            -fx-cursor: hand;
        """);
        
        // Title
        Label titleLabel = new Label(event.getName());
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-wrap-text: true;");
        titleLabel.setPrefWidth(320);
        
        // Date/Time
        Label dateTimeLabel = new Label(String.format("%s %s - %s", 
            event.getDate().toString(), 
            event.getStartTime().toString(), 
            event.getEndTime().toString()));
        dateTimeLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Location
        Label locationLabel = new Label(String.format("📍 %s, %s", event.getCity(), event.getLocation()));
        locationLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Available seats
        Label seatsLabel = new Label(String.format("🪑 %d places", event.getCapacity()));
        seatsLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        // Price
        Label priceLabel = new Label(String.format("💰 %.2f TND", event.getPrice()));
        priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        
        // Map link
        Label mapLabel = new Label("🗺️ Voir sur la carte");
        mapLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #3b82f6; -fx-underline: true; -fx-cursor: hand;");
        
        card.getChildren().addAll(titleLabel, dateTimeLabel, locationLabel, seatsLabel, priceLabel, mapLabel);
        
        // Click handler
        card.setOnMouseClicked(e -> {
            EventContext.setSelectedEventId(event.getId());
            Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Détails Réservation Padel");
        });
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #f8fafc;
            -fx-border-color: #3b82f6;
            -fx-border-width: 2;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 4);
            -fx-cursor: hand;
        """));
        
        card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e5e7eb;
            -fx-border-width: 1;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 15;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);
            -fx-cursor: hand;
        """));
        
        return card;
    }
}
