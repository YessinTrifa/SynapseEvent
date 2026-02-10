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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

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

    // Enterprises Tab
    @FXML private TableView<Entreprise> entreprisesTable;
    @FXML private TableColumn<Entreprise, String> entrepriseNameColumn;
    @FXML private TableColumn<Entreprise, String> entrepriseSiretColumn;
    @FXML private TableColumn<Entreprise, Void> entrepriseActionColumn;


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
    @FXML private TableColumn<CustomEventRequest, String> requestTypeColumn;
    @FXML private TableColumn<CustomEventRequest, LocalDate> requestDateColumn;
    @FXML private TableColumn<CustomEventRequest, Double> requestBudgetColumn;
    @FXML private TableColumn<CustomEventRequest, Integer> requestCapacityColumn;
    @FXML private TableColumn<CustomEventRequest, String> requestLocationColumn;
    @FXML private TableColumn<CustomEventRequest, String> requestDetailsColumn;
    @FXML private TableColumn<CustomEventRequest, String> requestReasonColumn;
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
    private EntrepriseService entrepriseService = new EntrepriseService();
    private RoleService roleService = new RoleService();
    private List<EventInstance> allEvents = new ArrayList<>();

    @FXML
    public void initialize() {
        setupUsersTable();
        setupEnterprisesTable();
        setupBookingsTable();
        setupCustomRequestsTable();
        setupEventsTable();

        loadUsers();
        loadEnterprises();
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
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    editUser(user);
                });
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
                    setGraphic(buttonBox);
                }
            }
        });
    }


    private void setupBookingsTable() {
        bookingUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getNom()));
        
        // Show actual event name instead of eventType
        bookingTypeColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            String displayName = "Unknown Event";
            try {
                EventInstance instance = eventService.findbyId(booking.getEventId());
                if (instance != null) {
                    // Show the actual event name and type (e.g., "Partying - Summer Party")
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

        bookingActionColumn.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button approveButton = new Button("Approve");
            private final Button denyButton = new Button("Deny");
            private final HBox buttonBox = new HBox(5, approveButton, denyButton);

            {
                approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                denyButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                approveButton.setOnAction(actionEvent -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    approveBooking(booking);
                });

                denyButton.setOnAction(actionEvent -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    denyBooking(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    // Disable buttons if already processed
                    boolean isProcessed = "approved".equals(booking.getStatus()) || "denied".equals(booking.getStatus());
                    approveButton.setDisable(isProcessed);
                    denyButton.setDisable(isProcessed);
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void setupCustomRequestsTable() {
        requestUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getNom()));
        requestTypeColumn.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
        requestBudgetColumn.setCellValueFactory(new PropertyValueFactory<>("budget"));
        requestCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        requestLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        requestDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        requestReasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        requestActionColumn.setCellFactory(param -> new TableCell<CustomEventRequest, Void>() {
            private final Button approveButton = new Button("Approve");
            private final Button denyButton = new Button("Deny");
            private final HBox buttonBox = new HBox(5, approveButton, denyButton);

            {
                approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                denyButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                approveButton.setOnAction(actionEvent -> {
                    CustomEventRequest request = getTableView().getItems().get(getIndex());
                    approveRequest(request);
                });

                denyButton.setOnAction(actionEvent -> {
                    CustomEventRequest request = getTableView().getItems().get(getIndex());
                    denyRequest(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CustomEventRequest request = getTableView().getItems().get(getIndex());
                    // Disable buttons if already processed
                    boolean isProcessed = "approved".equals(request.getStatus()) || "denied".equals(request.getStatus());
                    approveButton.setDisable(isProcessed);
                    denyButton.setDisable(isProcessed);
                    setGraphic(buttonBox);
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

        // Add action buttons for events
        eventActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editButton.setOnAction(actionEvent -> {
                    EventInstance event = getTableView().getItems().get(getIndex());
                    editEvent(event);
                });

                deleteButton.setOnAction(actionEvent -> {
                    EventInstance event = getTableView().getItems().get(getIndex());
                    deleteEvent(event);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void setupEnterprisesTable() {
        entrepriseNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        entrepriseSiretColumn.setCellValueFactory(new PropertyValueFactory<>("siret"));

        entrepriseActionColumn.setCellFactory(param -> new TableCell<Entreprise, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Entreprise entreprise = getTableView().getItems().get(getIndex());
                    editEntreprise(entreprise);
                });
                deleteButton.setOnAction(event -> {
                    Entreprise entreprise = getTableView().getItems().get(getIndex());
                    deleteEntreprise(entreprise);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }

    private void loadEnterprises() {
        entreprisesTable.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(userService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");

        // Create form fields
        TextField nomField = new TextField();
        nomField.setPromptText("Last Name");

        TextField prenomField = new TextField();
        prenomField.setPromptText("First Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (optional)");

        TextField addressField = new TextField();
        addressField.setPromptText("Address (optional)");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "User", "Manager");
        roleCombo.setValue("User");
        roleCombo.setPromptText("Role");

        ComboBox<Entreprise> enterpriseCombo = new ComboBox<>();
        enterpriseCombo.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
        enterpriseCombo.setPromptText("Enterprise");
        if (!enterpriseCombo.getItems().isEmpty()) {
            enterpriseCombo.setValue(enterpriseCombo.getItems().get(0));
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Address:"), 0, 5);
        grid.add(addressField, 1, 5);
        grid.add(new Label("Role:"), 0, 6);
        grid.add(roleCombo, 1, 6);
        grid.add(new Label("Enterprise:"), 0, 7);
        grid.add(enterpriseCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() || passwordField.getText().isEmpty() ||
                    roleCombo.getValue() == null || enterpriseCombo.getValue() == null) {
                    showAlert("Error", "Please fill in all required fields");
                    return null;
                }

                try {
                    Role role = roleService.getByName(roleCombo.getValue());
                    Entreprise enterprise = enterpriseCombo.getValue();

                    if (userService.findByEmail(emailField.getText().trim()) != null) {
                        showAlert("Error", "Email already exists");
                        return null;
                    }

                    User newUser = new User(emailField.getText().trim(), passwordField.getText(),
                        nomField.getText().trim(), prenomField.getText().trim(),
                        phoneField.getText().trim(), addressField.getText().trim(),
                        null, role, enterprise);
                    return newUser;
                } catch (Exception e) {
                    showAlert("Error", "Error creating user: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            if (user != null) {
                try {
                    userService.ajouter(user);
                    loadUsers();
                    showAlert("Success", "User created successfully");
                } catch (SQLException e) {
                    showAlert("Error", "Error saving user: " + e.getMessage());
                }
            }
        });
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

    private void editUser(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + user.getEmail());

        // Create form fields
        TextField nomField = new TextField(user.getNom());
        nomField.setPromptText("Last Name");

        TextField prenomField = new TextField(user.getPrenom());
        prenomField.setPromptText("First Name");

        TextField emailField = new TextField(user.getEmail());
        emailField.setPromptText("Email");

        TextField phoneField = new TextField(user.getPhone());
        phoneField.setPromptText("Phone (optional)");

        TextField addressField = new TextField(user.getAddress());
        addressField.setPromptText("Address (optional)");

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Admin", "User", "Manager");
        roleCombo.setValue(user.getRole().getName());
        roleCombo.setPromptText("Role");

        ComboBox<Entreprise> enterpriseCombo = new ComboBox<>();
        enterpriseCombo.setItems(FXCollections.observableArrayList(entrepriseService.getAll()));
        enterpriseCombo.setValue(user.getEnterprise());
        enterpriseCombo.setPromptText("Enterprise");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Last Name:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Role:"), 0, 5);
        grid.add(roleCombo, 1, 5);
        grid.add(new Label("Enterprise:"), 0, 6);
        grid.add(enterpriseCombo, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (nomField.getText().trim().isEmpty() || prenomField.getText().trim().isEmpty() ||
                    emailField.getText().trim().isEmpty() || roleCombo.getValue() == null ||
                    enterpriseCombo.getValue() == null) {
                    showAlert("Error", "Please fill in all required fields");
                    return null;
                }

                user.setNom(nomField.getText().trim());
                user.setPrenom(prenomField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setAddress(addressField.getText().trim());

                Role role = roleService.getByName(roleCombo.getValue());
                if (role != null) {
                    user.setRole(role);
                }
                user.setEnterprise(enterpriseCombo.getValue());

                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(editedUser -> {
            try {
                userService.modifier(editedUser);
                loadUsers();
                showAlert("Success", "User updated successfully");
            } catch (SQLException e) {
                showAlert("Error", "Error updating user: " + e.getMessage());
            }
        });
    }

    @FXML
    private void createEnterprise() {
        addEnterprise();
    }

    @FXML
    private void addEnterprise() {
        Dialog<Entreprise> dialog = new Dialog<>();
        dialog.setTitle("Add New Enterprise");
        dialog.setHeaderText("Create a new enterprise/company");

        TextField nameField = new TextField();
        nameField.setPromptText("Enterprise Name");

        TextField siretField = new TextField();
        siretField.setPromptText("SIRET Number");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("SIRET:"), 0, 1);
        grid.add(siretField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (nameField.getText().trim().isEmpty() || siretField.getText().trim().isEmpty()) {
                    showAlert("Error", "Please fill in all required fields");
                    return null;
                }
                Entreprise entreprise = new Entreprise();
                entreprise.setNom(nameField.getText().trim());
                entreprise.setSiret(siretField.getText().trim());
                return entreprise;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(entreprise -> {
            if (entreprise != null) {
                entrepriseService.add(entreprise);
                loadEnterprises();
                showAlert("Success", "Enterprise created successfully");
            }
        });
    }

    private void editEntreprise(Entreprise entreprise) {
        Dialog<Entreprise> dialog = new Dialog<>();
        dialog.setTitle("Edit Enterprise");
        dialog.setHeaderText("Edit enterprise: " + entreprise.getNom());

        TextField nameField = new TextField(entreprise.getNom());
        nameField.setPromptText("Enterprise Name");

        TextField siretField = new TextField(entreprise.getSiret());
        siretField.setPromptText("SIRET Number");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("SIRET:"), 0, 1);
        grid.add(siretField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                if (nameField.getText().trim().isEmpty() || siretField.getText().trim().isEmpty()) {
                    showAlert("Error", "Please fill in all required fields");
                    return null;
                }
                entreprise.setNom(nameField.getText().trim());
                entreprise.setSiret(siretField.getText().trim());
                return entreprise;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(editedEntreprise -> {
            entrepriseService.update(editedEntreprise);
            loadEnterprises();
            showAlert("Success", "Enterprise updated successfully");
        });
    }

    private void deleteEntreprise(Entreprise entreprise) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Enterprise");
        confirmDialog.setHeaderText("Are you sure you want to delete enterprise: " + entreprise.getNom() + "?");
        confirmDialog.setContentText("This action cannot be undone. Users associated with this enterprise may be affected.");

        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                entrepriseService.delete(entreprise.getId());
                loadEnterprises();
                showAlert("Success", "Enterprise deleted successfully");
            }
        });
    }

    private void editEvent(EventInstance event) {
        // Show a dialog to edit event details
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Edit Event");
        dialog.setHeaderText("Edit event: " + event.getName());
        
        // Create form fields
        TextField nameField = new TextField(event.getName());
        nameField.setPromptText("Event Name");
        
        DatePicker datePicker = new DatePicker(event.getDate());
        datePicker.setPromptText("Event Date");
        
        TextField locationField = new TextField(event.getLocation());
        locationField.setPromptText("Location");
        
        Spinner<Integer> capacitySpinner = new Spinner<>(1, 1000, event.getCapacity() != null ? event.getCapacity() : 50);
        capacitySpinner.setPromptText("Capacity");
        
        Spinner<Double> priceSpinner = new Spinner<>(0.0, 10000.0, event.getPrice() != null ? event.getPrice() : 0.0, 10.0);
        priceSpinner.setPromptText("Price");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("draft", "published", "pending", "confirmed", "cancelled");
        statusCombo.setValue(event.getStatus());
        
        TextArea descriptionArea = new TextArea(event.getDescription());
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(3);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3);
        grid.add(capacitySpinner, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(priceSpinner, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionArea, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new String[]{
                    nameField.getText(),
                    datePicker.getValue() != null ? datePicker.getValue().toString() : "",
                    locationField.getText(),
                    String.valueOf(capacitySpinner.getValue()),
                    String.valueOf(priceSpinner.getValue()),
                    statusCombo.getValue(),
                    descriptionArea.getText()
                };
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (!result[0].trim().isEmpty()) {
                event.setName(result[0]);
                event.setDate(LocalDate.parse(result[1]));
                event.setLocation(result[2]);
                event.setCapacity(Integer.parseInt(result[3]));
                event.setPrice(Double.parseDouble(result[4]));
                event.setStatus(result[5]);
                event.setDescription(result[6]);
                
                try {
                    eventService.modifier(event);
                    loadEvents();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteEvent(EventInstance event) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Event");
        confirmDialog.setHeaderText("Are you sure you want to delete event: " + event.getName() + "?");
        confirmDialog.setContentText("This action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    eventService.supprimer(event);
                    loadEvents();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void editRole(User user) {
        ChoiceDialog<String> roleDialog = new ChoiceDialog<>(
            user.getRole().getName(),
            "Admin", "User", "Organizer", "Manager"
        );
        roleDialog.setTitle("Change User Role");
        roleDialog.setHeaderText("Select new role for: " + user.getNom());
        roleDialog.setContentText("Role:");

        roleDialog.showAndWait().ifPresent(newRole -> {
            try {
                Role role = new RoleService().getByName(newRole);
                if (role != null) {
                    user.setRole(role);
                    userService.modifier(user);
                    loadUsers();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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

    private void approveBooking(Booking booking) {
        try {
            booking.setStatus("approved");
            bookingService.modifier(booking);
            
            // Also update the event instance status to "confirmed"
            EventInstance event = eventService.findbyId(booking.getEventId());
            if (event != null) {
                event.setStatus("confirmed");
                eventService.modifier(event);
            }
            
            loadBookings();
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void denyBooking(Booking booking) {
        try {
            booking.setStatus("denied");
            bookingService.modifier(booking);
            
            // Also update the event instance status back to "published" (no pending bookings)
            EventInstance event = eventService.findbyId(booking.getEventId());
            if (event != null) {
                // Check if there are other pending bookings for this event
                List<Booking> eventBookings = bookingService.getBookingsByEvent("instance", booking.getEventId());
                boolean hasPending = eventBookings.stream()
                    .anyMatch(b -> "pending".equals(b.getStatus()) && !b.getId().equals(booking.getId()));
                
                if (hasPending) {
                    event.setStatus("pending");
                } else {
                    event.setStatus("published");
                }
                eventService.modifier(event);
            }
            
            loadBookings();
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void approveRequest(CustomEventRequest request) {
        try {
            request.setStatus("approved");
            customRequestService.updateStatus(request.getId(), "approved", null);
            loadCustomRequests();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void denyRequest(CustomEventRequest request) {
        // Show a dialog to enter the reason for denial
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Deny Request");
        dialog.setHeaderText("Please provide a reason for denying this request");
        dialog.setContentText("Reason:");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter the reason for denial...");
        reasonArea.setPrefRowCount(3);

        dialog.getDialogPane().setContent(reasonArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return reasonArea.getText().trim();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(reason -> {
            try {
                customRequestService.updateStatus(request.getId(), "denied", reason);
                loadCustomRequests();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void toggleRequestStatus(CustomEventRequest request) {
        // Kept for backward compatibility, but recommend using approveRequest/denyRequest
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