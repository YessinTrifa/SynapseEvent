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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class AdminDashboardController {

    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TableColumn<User, Void> userActionColumn;


    // Bookings Tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingUserColumn;
    @FXML private TableColumn<Booking, String> bookingTypeColumn;
    @FXML private TableColumn<Booking, Long> bookingEventIdColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    @FXML private TableColumn<Booking, Void> bookingActionColumn;

    // Custom Requests Tab
    @FXML private TableView<CustomEventRequest> customRequestsTable;
    @FXML private TableColumn<CustomEventRequest, String> requestUserColumn;
    @FXML private TableColumn<CustomEventRequest, String> requestDetailsColumn;
    @FXML private TableColumn<CustomEventRequest, LocalDate> requestDateColumn;
    @FXML private TableColumn<CustomEventRequest, String> requestStatusColumn;
    @FXML private TableColumn<CustomEventRequest, Void> requestActionColumn;


    // Events Tab (EventInstances)
    @FXML private TableView<EventInstance> eventsTable;
    @FXML private TableColumn<EventInstance, String> eventNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> eventDateColumn;
    @FXML private TableColumn<EventInstance, String> eventLocationColumn;
    @FXML private TableColumn<EventInstance, Integer> eventCapacityColumn;
    @FXML private TableColumn<EventInstance, Double> eventPriceColumn;
    @FXML private TableColumn<EventInstance, String> eventStatusColumn;
    @FXML private TableColumn<EventInstance, String> eventTypeColumn;
    @FXML private TableColumn<EventInstance, Void> eventActionColumn;
    @FXML private ComboBox<String> eventTypeFilter;

    private UserService userService = new UserService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();
    private EventInstanceService eventService = new EventInstanceService();
    private List<EventInstance> allEvents = new ArrayList<>();

    @FXML
    public void initialize() {
        setupUsersTable();
        setupBookingsTable();
        setupCustomRequestsTable();
        setupEventsTable();

        loadUsers();
        loadBookings();
        loadCustomRequests();
        loadEvents();
        setupEventTypeFilter();
    }

    private void setupUsersTable() {
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().getName()));

        userActionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }


    private void setupBookingsTable() {
        bookingUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getNom()));
        bookingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        bookingEventIdColumn.setCellValueFactory(new PropertyValueFactory<>("eventId"));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        bookingActionColumn.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    toggleBookingStatus(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    actionButton.setText("approved".equals(booking.getStatus()) ? "Reject" : "Approve");
                    setGraphic(actionButton);
                }
            }
        });
    }

    private void setupCustomRequestsTable() {
        requestUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getNom()));
        requestDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        requestActionColumn.setCellFactory(param -> new TableCell<CustomEventRequest, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    CustomEventRequest request = getTableView().getItems().get(getIndex());
                    toggleRequestStatus(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CustomEventRequest request = getTableView().getItems().get(getIndex());
                    actionButton.setText("approved".equals(request.getStatus()) ? "Reject" : "Approve");
                    setGraphic(actionButton);
                }
            }
        });
    }





    private void setupEventsTable() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        eventLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        eventCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        eventStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        eventTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        // No action buttons for events - managed by specific controllers
    }


    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(userService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadBookings() {
        try {
            bookingsTable.setItems(FXCollections.observableArrayList(bookingService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomRequests() {
        try {
            customRequestsTable.setItems(FXCollections.observableArrayList(customRequestService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadEvents() {
        try {
            allEvents = eventService.readAll();
            eventsTable.setItems(FXCollections.observableArrayList(allEvents));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupEventTypeFilter() {
        eventTypeFilter.getItems().add("All");
        eventTypeFilter.getItems().addAll("Formation", "Paddle", "Partying", "TeamBuilding", "Anniversary");
        eventTypeFilter.setValue("All");
    }

    @FXML
    private void filterEventsByType() {
        String selectedType = eventTypeFilter.getValue();
        if ("All".equals(selectedType)) {
            eventsTable.setItems(FXCollections.observableArrayList(allEvents));
        } else {
            List<EventInstance> filtered = allEvents.stream()
                .filter(event -> selectedType.equals(event.getType()))
                .collect(java.util.stream.Collectors.toList());
            eventsTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }


    private void deleteUser(User user) {
        try {
            userService.supprimer(user);
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    private void toggleBookingStatus(Booking booking) {
        try {
            booking.setStatus("approved".equals(booking.getStatus()) ? "pending" : "approved");
            bookingService.modifier(booking);
            loadBookings();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleRequestStatus(CustomEventRequest request) {
        try {
            request.setStatus("approved".equals(request.getStatus()) ? "pending" : "approved");
            customRequestService.modifier(request);
            loadCustomRequests();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createFormationEvent() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/formation.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createPaddleEvent() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/paddle.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createPartyingEvent() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/partying.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createTeamBuildingEvent() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/teamBuilding.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createAnniversaryEvent() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/anniversary.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createEvent() {
        // Show a dialog to select event type
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Formation", "Formation", "Paddle", "Partying", "TeamBuilding", "Anniversary", "Create New Type");
        dialog.setTitle("Create Event");
        dialog.setHeaderText("Select Event Type");
        dialog.setContentText("Choose the type of event to create:");

        dialog.showAndWait().ifPresent(eventType -> {
            if ("Create New Type".equals(eventType)) {
                TextInputDialog newTypeDialog = new TextInputDialog();
                newTypeDialog.setTitle("Create New Event Type");
                newTypeDialog.setHeaderText("Enter the name of the new event type:");
                newTypeDialog.setContentText("Type:");

                newTypeDialog.showAndWait().ifPresent(newType -> {
                    if (!newType.trim().isEmpty()) {
                        // For now, just show a message. In a full implementation, you'd create a new FXML or handle it.
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("New Type Created");
                        alert.setHeaderText("New event type '" + newType + "' created.");
                        alert.setContentText("Note: Creation forms for new types need to be implemented.");
                        alert.showAndWait();
                        // Optionally, add to the filter
                        if (!eventTypeFilter.getItems().contains(newType)) {
                            eventTypeFilter.getItems().add(newType);
                        }
                    }
                });
            } else {
                switch (eventType) {
                    case "Formation":
                        createFormationEvent();
                        break;
                    case "Paddle":
                        createPaddleEvent();
                        break;
                    case "Partying":
                        createPartyingEvent();
                        break;
                    case "TeamBuilding":
                        createTeamBuildingEvent();
                        break;
                    case "Anniversary":
                        createAnniversaryEvent();
                        break;
                }
            }
        });
    }

    @FXML
    private void logout() {
        CurrentUser.logout();
        loadFXML("/fxml/login.fxml");
    }

    private void loadFXML(String fxmlPath) {
        try {
            Stage stage = (Stage) usersTable.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}