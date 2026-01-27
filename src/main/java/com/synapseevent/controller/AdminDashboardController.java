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
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {

    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TableColumn<User, Void> userActionColumn;

    // Events Tabs - Anniversary
    @FXML private TableView<AnniversaryEvent> anniversaryTable;
    @FXML private TableColumn<AnniversaryEvent, String> anniversaryNameColumn;
    @FXML private TableColumn<AnniversaryEvent, LocalDate> anniversaryDateColumn;
    @FXML private TableColumn<AnniversaryEvent, String> anniversaryDescriptionColumn;
    @FXML private TableColumn<AnniversaryEvent, String> anniversaryStatusColumn;
    @FXML private TableColumn<AnniversaryEvent, Void> anniversaryActionColumn;

    // Formation
    @FXML private TableView<FormationEvent> formationTable;
    @FXML private TableColumn<FormationEvent, String> formationNameColumn;
    @FXML private TableColumn<FormationEvent, LocalDate> formationDateColumn;
    @FXML private TableColumn<FormationEvent, String> formationDescriptionColumn;
    @FXML private TableColumn<FormationEvent, String> formationStatusColumn;
    @FXML private TableColumn<FormationEvent, Void> formationActionColumn;

    // Paddle
    @FXML private TableView<PaddleEvent> paddleTable;
    @FXML private TableColumn<PaddleEvent, String> paddleNameColumn;
    @FXML private TableColumn<PaddleEvent, LocalDate> paddleDateColumn;
    @FXML private TableColumn<PaddleEvent, String> paddleDescriptionColumn;
    @FXML private TableColumn<PaddleEvent, String> paddleStatusColumn;
    @FXML private TableColumn<PaddleEvent, Void> paddleActionColumn;

    // Partying
    @FXML private TableView<PartyingEvent> partyingTable;
    @FXML private TableColumn<PartyingEvent, String> partyingNameColumn;
    @FXML private TableColumn<PartyingEvent, LocalDate> partyingDateColumn;
    @FXML private TableColumn<PartyingEvent, String> partyingDescriptionColumn;
    @FXML private TableColumn<PartyingEvent, String> partyingStatusColumn;
    @FXML private TableColumn<PartyingEvent, Void> partyingActionColumn;

    // TeamBuilding
    @FXML private TableView<TeamBuildingEvent> teamBuildingTable;
    @FXML private TableColumn<TeamBuildingEvent, String> teamBuildingNameColumn;
    @FXML private TableColumn<TeamBuildingEvent, LocalDate> teamBuildingDateColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> teamBuildingDescriptionColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> teamBuildingStatusColumn;
    @FXML private TableColumn<TeamBuildingEvent, Void> teamBuildingActionColumn;

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

    private UserService userService = new UserService();
    private AnniversaryEventService anniversaryService = new AnniversaryEventService();
    private FormationEventService formationService = new FormationEventService();
    private PaddleEventService paddleService = new PaddleEventService();
    private PartyingEventService partyingService = new PartyingEventService();
    private TeamBuildingEventService teamBuildingService = new TeamBuildingEventService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();

    @FXML
    public void initialize() {
        setupUsersTable();
        setupAnniversaryTable();
        setupFormationTable();
        setupPaddleTable();
        setupPartyingTable();
        setupTeamBuildingTable();
        setupBookingsTable();
        setupCustomRequestsTable();

        loadUsers();
        loadAnniversaryEvents();
        loadFormationEvents();
        loadPaddleEvents();
        loadPartyingEvents();
        loadTeamBuildingEvents();
        loadBookings();
        loadCustomRequests();
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

    private void setupAnniversaryTable() {
        anniversaryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        anniversaryDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        anniversaryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        anniversaryStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        anniversaryActionColumn.setCellFactory(param -> new TableCell<AnniversaryEvent, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    AnniversaryEvent event = getTableView().getItems().get(getIndex());
                    togglePublishAnniversary(event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AnniversaryEvent event = getTableView().getItems().get(getIndex());
                    actionButton.setText("published".equals(event.getStatus()) ? "Unpublish" : "Publish");
                    setGraphic(actionButton);
                }
            }
        });
    }

    private void setupFormationTable() {
        formationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        formationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        formationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        formationActionColumn.setCellFactory(param -> new TableCell<FormationEvent, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    FormationEvent e = getTableView().getItems().get(getIndex());
                    togglePublishFormation(e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    FormationEvent e = getTableView().getItems().get(getIndex());
                    actionButton.setText("published".equals(e.getStatus()) ? "Unpublish" : "Publish");
                    setGraphic(actionButton);
                }
            }
        });
    }

    private void setupPaddleTable() {
        paddleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        paddleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        paddleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        paddleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        paddleActionColumn.setCellFactory(param -> new TableCell<PaddleEvent, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    PaddleEvent e = getTableView().getItems().get(getIndex());
                    togglePublishPaddle(e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PaddleEvent e = getTableView().getItems().get(getIndex());
                    actionButton.setText("published".equals(e.getStatus()) ? "Unpublish" : "Publish");
                    setGraphic(actionButton);
                }
            
            }
        });
    }

    private void setupPartyingTable() {
        partyingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partyingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        partyingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        partyingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        partyingActionColumn.setCellFactory(param -> new TableCell<PartyingEvent, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    PartyingEvent e = getTableView().getItems().get(getIndex());
                    togglePublishPartying(e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PartyingEvent e = getTableView().getItems().get(getIndex());
                    actionButton.setText("published".equals(e.getStatus()) ? "Unpublish" : "Publish");
                    setGraphic(actionButton);
                }
            
            }
        });
    }

    private void setupTeamBuildingTable() {
        teamBuildingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamBuildingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        teamBuildingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        teamBuildingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        teamBuildingActionColumn.setCellFactory(param -> new TableCell<TeamBuildingEvent, Void>() {
            private final Button actionButton = new Button();

            {
                actionButton.setOnAction(actionEvent -> {
                    TeamBuildingEvent e = getTableView().getItems().get(getIndex());
                    togglePublishTeamBuilding(e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TeamBuildingEvent e = getTableView().getItems().get(getIndex());
                    actionButton.setText("published".equals(e.getStatus()) ? "Unpublish" : "Publish");
                    setGraphic(actionButton);
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

    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(userService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAnniversaryEvents() {
        try {
            anniversaryTable.setItems(FXCollections.observableArrayList(anniversaryService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFormationEvents() {
        try {
            formationTable.setItems(FXCollections.observableArrayList(formationService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaddleEvents() {
        try {
            paddleTable.setItems(FXCollections.observableArrayList(paddleService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPartyingEvents() {
        try {
            partyingTable.setItems(FXCollections.observableArrayList(partyingService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTeamBuildingEvents() {
        try {
            teamBuildingTable.setItems(FXCollections.observableArrayList(teamBuildingService.readAll()));
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

    private void deleteUser(User user) {
        try {
            userService.supprimer(user);
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePublishAnniversary(AnniversaryEvent event) {
        try {
            event.setStatus("published".equals(event.getStatus()) ? "draft" : "published");
            anniversaryService.modifier(event);
            loadAnniversaryEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePublishFormation(FormationEvent event) {
        try {
            event.setStatus("published".equals(event.getStatus()) ? "draft" : "published");
            formationService.modifier(event);
            loadFormationEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePublishPaddle(PaddleEvent event) {
        try {
            event.setStatus("published".equals(event.getStatus()) ? "draft" : "published");
            paddleService.modifier(event);
            loadPaddleEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePublishPartying(PartyingEvent event) {
        try {
            event.setStatus("published".equals(event.getStatus()) ? "draft" : "published");
            partyingService.modifier(event);
            loadPartyingEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void togglePublishTeamBuilding(TeamBuildingEvent event) {
        try {
            event.setStatus("published".equals(event.getStatus()) ? "draft" : "published");
            teamBuildingService.modifier(event);
            loadTeamBuildingEvents();
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