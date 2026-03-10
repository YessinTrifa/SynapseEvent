package com.synapseevent.controller;

import com.synapseevent.entities.EventInstance;
import com.synapseevent.service.EventInstanceService;
import com.synapseevent.utils.Navigator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class GlobalCalendarController {

    // ── FXML nodes ──────────────────────────────────────────────────────────
    @FXML
    private Label monthYearLabel;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private VBox eventListPanel;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private VBox eventListContainer;
    @FXML
    private Label legendAnniversary;
    @FXML
    private Label legendFormation;
    @FXML
    private Label legendPaddle;
    @FXML
    private Label legendPartying;
    @FXML
    private Label legendTeamBuilding;
    @FXML
    private Label legendCustom;

    // ── State ────────────────────────────────────────────────────────────────
    private YearMonth currentYearMonth;
    private Map<LocalDate, List<EventInstance>> eventsByDate = new HashMap<>();
    private final EventInstanceService eventInstanceService = new EventInstanceService();

    // ── Colour palette per event type ────────────────────────────────────────
    private static final Map<String, String> TYPE_COLOR = new LinkedHashMap<>();
    static {
        TYPE_COLOR.put("Anniversary", "#f43f5e"); // rose-500
        TYPE_COLOR.put("Formation", "#3b82f6"); // blue-500
        TYPE_COLOR.put("Paddle", "#10b981"); // emerald-500
        TYPE_COLOR.put("Partying", "#f59e0b"); // amber-500
        TYPE_COLOR.put("TeamBuilding", "#8b5cf6"); // violet-500
        TYPE_COLOR.put("Custom", "#6b7280"); // gray-500 (fallback)
    }

    // ── Initialization ───────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now();
        loadAllEvents();
        buildCalendar();
    }

    private void loadAllEvents() {
        try {
            List<EventInstance> all = eventInstanceService.readAll();
            java.util.Set<String> seenEvents = new java.util.HashSet<>();
            eventsByDate = all.stream()
                    .filter(e -> e.getDate() != null)
                    .filter(e -> {
                        String key = e.getName() + "|" + e.getDate();
                        return seenEvents.add(key);
                    })
                    .collect(Collectors.groupingBy(EventInstance::getDate));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Could not load events: " + e.getMessage());
        }
    }

    // ── Calendar building ────────────────────────────────────────────────────
    private void buildCalendar() {
        monthYearLabel.setText(
                currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        + " " + currentYearMonth.getYear());

        calendarGrid.getChildren().clear();
        // Keep the header row (day names) – they are already in the FXML.
        // We add day cells starting at row 1.

        LocalDate firstDay = currentYearMonth.atDay(1);
        int startCol = firstDay.getDayOfWeek().getValue() % 7; // Mon=1..Sun=0 → Sun=0
        // We want: Sun=0, Mon=1, … Sat=6
        // DayOfWeek: MON=1…SUN=7 → (value % 7) gives SUN=0, MON=1 … SAT=6
        int daysInMonth = currentYearMonth.lengthOfMonth();

        int col = startCol;
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            List<EventInstance> events = eventsByDate.getOrDefault(date, Collections.emptyList());

            VBox cell = buildDayCell(date, events);
            calendarGrid.add(cell, col, row);

            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }

        // Hide the event list panel until the user clicks a day
        eventListPanel.setVisible(false);
        eventListPanel.setManaged(false);
    }

    /** Create a single calendar day cell. */
    private VBox buildDayCell(LocalDate date, List<EventInstance> events) {
        VBox cell = new VBox(3);
        cell.setAlignment(Pos.TOP_CENTER);
        cell.setPadding(new Insets(6, 4, 6, 4));
        cell.setPrefWidth(130);
        cell.setMinHeight(80);

        boolean isToday = date.equals(LocalDate.now());
        boolean hasEvents = !events.isEmpty();

        // Day number label
        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        if (isToday) {
            // Highlight today with a circle
            StackPane dayCircle = new StackPane();
            Circle circle = new Circle(14, Color.web("#6366f1"));
            dayLabel.setTextFill(Color.WHITE);
            dayCircle.getChildren().addAll(circle, dayLabel);
            cell.getChildren().add(dayCircle);
            cell.setStyle(
                    "-fx-border-color: #6366f1;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-radius: 10;" +
                            "-fx-background-color: #f5f3ff;");
        } else {
            dayLabel.setTextFill(Color.web("#1f2937"));
            cell.getChildren().add(dayLabel);
            cell.setStyle(
                    "-fx-border-color: #e5e7eb;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 8;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #ffffff;");
        }

        // Coloured dot row for events (max 4 dots + overflow label)
        if (hasEvents) {
            FlowPane dots = new FlowPane(3, 2);
            dots.setAlignment(Pos.CENTER);
            int shown = 0;
            for (EventInstance ei : events) {
                if (shown >= 4)
                    break;
                Circle dot = new Circle(5, Color.web(colorForType(ei.getType())));
                Tooltip.install(dot, new Tooltip(ei.getName() + " (" + ei.getType() + ")"));
                dots.getChildren().add(dot);
                shown++;
            }
            if (events.size() > 4) {
                Label more = new Label("+" + (events.size() - 4));
                more.setFont(Font.font("System", 9));
                more.setTextFill(Color.web("#6b7280"));
                dots.getChildren().add(more);
            }
            cell.getChildren().add(dots);

            // Hover effect
            cell.setOnMouseEntered(e -> {
                if (!isToday)
                    cell.setStyle(
                            "-fx-border-color: #6366f1; -fx-border-width: 1;" +
                                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                                    "-fx-background-color: #ede9fe; -fx-cursor: hand;");
            });
            cell.setOnMouseExited(e -> {
                if (!isToday)
                    cell.setStyle(
                            "-fx-border-color: #e5e7eb; -fx-border-width: 1;" +
                                    "-fx-border-radius: 8; -fx-background-radius: 8;" +
                                    "-fx-background-color: #ffffff;");
            });
        }

        // Click handler → show event list panel
        List<EventInstance> snapshot = new ArrayList<>(events);
        cell.setOnMouseClicked(e -> showEventsForDate(date, snapshot));
        cell.setStyle(cell.getStyle() + " -fx-cursor: hand;");

        return cell;
    }

    /** Populate the right-side event list panel for the clicked date. */
    private void showEventsForDate(LocalDate date, List<EventInstance> events) {
        selectedDateLabel.setText(
                date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " +
                        date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " +
                        date.getDayOfMonth() + " " + date.getYear());

        eventListContainer.getChildren().clear();

        if (events.isEmpty()) {
            Label none = new Label("No events on this day.");
            none.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13;");
            eventListContainer.getChildren().add(none);
        } else {
            for (EventInstance ei : events) {
                eventListContainer.getChildren().add(buildEventCard(ei));
            }
        }

        eventListPanel.setVisible(true);
        eventListPanel.setManaged(true);
    }

    /** Build a compact card widget for one event. */
    private HBox buildEventCard(EventInstance ei) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);");

        // Left colour stripe
        Region stripe = new Region();
        stripe.setPrefWidth(5);
        stripe.setPrefHeight(48);
        stripe.setStyle("-fx-background-color: " + colorForType(ei.getType()) + ";" +
                "-fx-background-radius: 3;");

        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(ei.getName() != null ? ei.getName() : "Unnamed Event");
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.web("#111827"));

        HBox meta = new HBox(10);
        meta.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(ei.getType() != null ? ei.getType() : "—");
        typeLabel.setStyle(
                "-fx-background-color: " + colorForType(ei.getType()) + "22;" +
                        "-fx-text-fill: " + colorForType(ei.getType()) + ";" +
                        "-fx-padding: 2 8 2 8;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-size: 11; -fx-font-weight: bold;");

        String time = "";
        if (ei.getStartTime() != null) {
            time = ei.getStartTime().toString();
            if (ei.getEndTime() != null)
                time += " – " + ei.getEndTime().toString();
        }
        Label timeLabel = new Label(time.isEmpty() ? "All day" : time);
        timeLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");

        meta.getChildren().addAll(typeLabel, timeLabel);

        Label locationLabel = new Label("📍 " + (ei.getLocation() != null ? ei.getLocation() : "—"));
        locationLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11;");

        info.getChildren().addAll(nameLabel, meta, locationLabel);

        // Price badge
        VBox right = new VBox();
        right.setAlignment(Pos.CENTER_RIGHT);
        if (ei.getPrice() != null) {
            Label priceLabel = new Label(String.format("%.0f TND", ei.getPrice()));
            priceLabel.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold; -fx-font-size: 13;");
            right.getChildren().add(priceLabel);
        }
        String statusText = ei.getStatus() != null ? ei.getStatus().toUpperCase() : "";
        Label statusLabel = new Label(statusText);
        statusLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 10;");
        right.getChildren().add(statusLabel);

        card.getChildren().addAll(stripe, info, right);
        return card;
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    @FXML
    private void goBack() {
        Navigator.get().go("/fxml/userDashboard.fxml", "User Dashboard - SynapseEvent");
    }

    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        buildCalendar();
    }

    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        buildCalendar();
    }

    @FXML
    private void goToToday() {
        currentYearMonth = YearMonth.now();
        buildCalendar();
        // Immediately show today's events
        LocalDate today = LocalDate.now();
        showEventsForDate(today, eventsByDate.getOrDefault(today, Collections.emptyList()));
    }

    @FXML
    private void closeEventPanel() {
        eventListPanel.setVisible(false);
        eventListPanel.setManaged(false);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private String colorForType(String type) {
        if (type == null)
            return TYPE_COLOR.get("Custom");
        return TYPE_COLOR.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(type))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(TYPE_COLOR.get("Custom"));
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
