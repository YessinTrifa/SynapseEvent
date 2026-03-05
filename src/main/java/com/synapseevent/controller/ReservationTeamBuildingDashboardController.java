package com.synapseevent.controller;

import com.synapseevent.entities.TeamBuildingEvent;
import com.synapseevent.service.TeamBuildingEventService;
import java.sql.SQLException;
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

public class ReservationTeamBuildingDashboardController {
    
    @FXML private TilePane eventsTilePane;
    @FXML private Button refreshBtn;

    private final TeamBuildingEventService teamBuildingEventService = new TeamBuildingEventService();
    
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
        List<TeamBuildingEvent> events;

        try {
            events = teamBuildingEventService.getPublishedEvents();
            for (TeamBuildingEvent event : events) {
                VBox eventCard = createEventCard(event);
                eventsTilePane.getChildren().add(eventCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        if (events.isEmpty()) {
            Label noEventsLabel = new Label("Aucun événement Team Building disponible");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            eventsTilePane.getChildren().add(noEventsLabel);
        }
    }
    
    private VBox createEventCard(TeamBuildingEvent event) {
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
        Label locationLabel = new Label(String.format("📍 %s", event.getLocation()));
        locationLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Available seats
        Label seatsLabel = new Label(String.format("🪑 %d places", event.getCapacity()));
        seatsLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        // Price
        Label priceLabel = new Label(String.format("💰 %.2f TND", event.getPrice()));
        priceLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        
        // Team Building type indicator
        Label typeLabel = new Label("🤝 Team Building");
        typeLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        card.getChildren().addAll(titleLabel, dateTimeLabel, locationLabel, seatsLabel, priceLabel, typeLabel);
        
        // Click handler
        card.setOnMouseClicked(e -> {
            System.out.println("DEBUG: Clicked on event with ID: " + event.getId());
            EventContext.setSelectedEventId(event.getId());
            System.out.println("DEBUG: EventContext.setSelectedEventId(" + event.getId() + ")");
            Navigator.get().go("/fxml/reservationTeamBuildingDetails.fxml", "Détails Réservation Team Building");
        });
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color: #f0fdf4;
            -fx-border-color: #10b981;
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
