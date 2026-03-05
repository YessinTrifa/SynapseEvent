package com.synapseevent.controller;

import com.synapseevent.service.VenueService;
import com.synapseevent.service.PaddleEventService;
import com.synapseevent.entities.Court;
import com.synapseevent.entities.Event;
import com.synapseevent.utils.EventContext;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class ReservationPadelDashboardController {
    
    @FXML private TabPane mainTabPane;
    @FXML private TilePane courtsTilePane;
    @FXML private TilePane eventsTilePane;
    @FXML private Button refreshBtn;
    
    private final VenueService venueService = new VenueService();
    private final PaddleEventService paddleEventService = new PaddleEventService();
    
    @FXML
    public void initialize() {
        loadCourts();
        loadTournaments();
    }
    
    @FXML
    private void refreshEvents() {
        loadCourts();
        loadTournaments();
    }
    
    private void loadCourts() {
        courtsTilePane.getChildren().clear();
        
        List<Court> courts = venueService.getPaddleCourts();
        
        for (Court court : courts) {
            VBox courtCard = createCourtCard(court);
            courtsTilePane.getChildren().add(courtCard);
        }
        
        if (courts.isEmpty()) {
            Label noCourtsLabel = new Label("Aucun court de paddle disponible");
            noCourtsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            courtsTilePane.getChildren().add(noCourtsLabel);
        }
    }
    
    private void loadTournaments() {
        eventsTilePane.getChildren().clear();
        
        List<Event> events = paddleEventService.findPadelEventsAvailable();
        
        for (Event event : events) {
            VBox eventCard = createTournamentCard(event);
            eventsTilePane.getChildren().add(eventCard);
        }
        
        if (events.isEmpty()) {
            Label noEventsLabel = new Label("Aucun tournoi de paddle disponible");
            noEventsLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #6b7280; -fx-font-style: italic;");
            eventsTilePane.getChildren().add(noEventsLabel);
        }
    }
    
    private VBox createCourtCard(Court court) {
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
        
        // Court name with type icon
        String typeIcon = court.getIsIndoor() ? "🏟️" : "☀️";
        Label nameLabel = new Label(typeIcon + " " + court.getName());
        nameLabel.setStyle("-fx-font-size: 17; -fx-font-weight: bold; -fx-text-fill: #1f2937; -fx-wrap-text: true;");
        nameLabel.setPrefWidth(320);
        
        // Court type (Indoor/Outdoor)
        String courtType = court.getIsIndoor() ? "Interior" : "Extérieur";
        Label typeLabel = new Label("🏸 " + courtType);
        typeLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #059669; -fx-font-weight: bold;");
        
        // Venue name
        Label venueLabel = new Label("📍 " + court.getVenueName());
        venueLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // City
        Label cityLabel = new Label("🌆 " + (court.getVenueCity() != null ? court.getVenueCity() : ""));
        cityLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #6b7280;");
        
        // Price per hour
        Label priceLabel = new Label(String.format("💰 %.2f TND/heure", court.getPricePerHour()));
        priceLabel.setStyle("-fx-font-size: 15; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
        
        // Select button
        Button selectBtn = new Button("Sélectionner");
        selectBtn.setStyle("""
            -fx-background-color: #3b82f6;
            -fx-text-fill: white;
            -fx-padding: 8 16;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        """);
        
        card.getChildren().addAll(nameLabel, typeLabel, venueLabel, cityLabel, priceLabel, selectBtn);
        
        // Click handler for the card
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                EventContext.setSelectedCourtId(court.getId());
                Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Réserver le Court");
            }
        });
        
        // Button click handler
        selectBtn.setOnAction(e -> {
            EventContext.setSelectedCourtId(court.getId());
            Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Réserver le Court");
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
    
    private VBox createTournamentCard(Event event) {
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
        
        // Title with trophy icon
        Label titleLabel = new Label("🏆 " + event.getName());
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
        
        // Book button
        Button bookBtn = new Button("Réserver");
        bookBtn.setStyle("""
            -fx-background-color: #10b981;
            -fx-text-fill: white;
            -fx-padding: 8 16;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-font-weight: bold;
            -fx-cursor: hand;
        """);
        bookBtn.setOnAction(e -> {
            EventContext.setSelectedEventId(event.getId());
            Navigator.get().go("/fxml/reservationPadelDetails.fxml", "Détails Réservation Padel");
        });
        
        card.getChildren().addAll(titleLabel, dateTimeLabel, locationLabel, seatsLabel, priceLabel, bookBtn);
        
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
