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
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    // Formation Tab
    @FXML private TableView<EventInstance> formationTable;
    @FXML private TableColumn<EventInstance, String> formationNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> formationDateColumn;
    @FXML private TableColumn<EventInstance, String> formationDescriptionColumn;
    @FXML private TableColumn<EventInstance, String> formationStatusColumn;
    @FXML private TableColumn<EventInstance, Void> formationActionColumn;

    // Paddle Tab
    @FXML private TableView<EventInstance> paddleTable;
    @FXML private TableColumn<EventInstance, String> paddleNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> paddleDateColumn;
    @FXML private TableColumn<EventInstance, String> paddleDescriptionColumn;
    @FXML private TableColumn<EventInstance, String> paddleStatusColumn;
    @FXML private TableColumn<EventInstance, Void> paddleActionColumn;

    // Partying Tab
    @FXML private TableView<EventInstance> partyingTable;
    @FXML private TableColumn<EventInstance, String> partyingNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> partyingDateColumn;
    @FXML private TableColumn<EventInstance, String> partyingDescriptionColumn;
    @FXML private TableColumn<EventInstance, String> partyingStatusColumn;
    @FXML private TableColumn<EventInstance, Void> partyingActionColumn;

    // TeamBuilding Tab
    @FXML private TableView<EventInstance> teamBuildingTable;
    @FXML private TableColumn<EventInstance, String> teamBuildingNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> teamBuildingDateColumn;
    @FXML private TableColumn<EventInstance, String> teamBuildingDescriptionColumn;
    @FXML private TableColumn<EventInstance, String> teamBuildingStatusColumn;
    @FXML private TableColumn<EventInstance, Void> teamBuildingActionColumn;

    // Anniversary Tab
    @FXML private TableView<EventInstance> anniversaryTable;
    @FXML private TableColumn<EventInstance, String> anniversaryNameColumn;
    @FXML private TableColumn<EventInstance, LocalDate> anniversaryDateColumn;
    @FXML private TableColumn<EventInstance, String> anniversaryLocationColumn;
    @FXML private TableColumn<EventInstance, Integer> anniversaryCapacityColumn;
    @FXML private TableColumn<EventInstance, Double> anniversaryPriceColumn;
    @FXML private TableColumn<EventInstance, String> anniversaryStatusColumn;
    @FXML private TableColumn<EventInstance, Void> anniversaryActionColumn;

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

    private UserService userService = new UserService();
    private BookingService bookingService = new BookingService();
    private CustomEventRequestService customRequestService = new CustomEventRequestService();
    private EventInstanceService eventService = new EventInstanceService();

    @FXML
    public void initialize() {
        setupUsersTable();
        setupBookingsTable();
        setupCustomRequestsTable();
        setupFormationTable();
        setupPaddleTable();
        setupPartyingTable();
        setupTeamBuildingTable();
        setupAnniversaryTable();
        setupEventsTable();

        loadUsers();
        loadBookings();
        loadCustomRequests();
        loadFormationEvents();
        loadPaddleEvents();
        loadPartyingEvents();
        loadTeamBuildingEvents();
        loadAnniversaryEvents();
        loadEvents();
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

    private void setupCategoriesTable() {
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        categoryActionColumn.setCellFactory(param -> new TableCell<EventCategory, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventCategory category = getTableView().getItems().get(getIndex());
                    editCategory(category);
                });
                deleteButton.setOnAction(event -> {
                    EventCategory category = getTableView().getItems().get(getIndex());
                    deleteCategory(category);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupSubcategoriesTable() {
        subcategoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        subcategoryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        subcategoryCategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));

        subcategoryActionColumn.setCellFactory(param -> new TableCell<EventSubcategory, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventSubcategory subcategory = getTableView().getItems().get(getIndex());
                    editSubcategory(subcategory);
                });
                deleteButton.setOnAction(event -> {
                    EventSubcategory subcategory = getTableView().getItems().get(getIndex());
                    deleteSubcategory(subcategory);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupTypesTable() {
        typeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeSubcategoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubcategory().getName()));

        typeActionColumn.setCellFactory(param -> new TableCell<EventType, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventType type = getTableView().getItems().get(getIndex());
                    editType(type);
                });
                deleteButton.setOnAction(event -> {
                    EventType type = getTableView().getItems().get(getIndex());
                    deleteType(type);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupVariantsTable() {
        variantNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        variantDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        variantTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().getName()));

        variantActionColumn.setCellFactory(param -> new TableCell<EventVariant, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventVariant variant = getTableView().getItems().get(getIndex());
                    editVariant(variant);
                });
                deleteButton.setOnAction(event -> {
                    EventVariant variant = getTableView().getItems().get(getIndex());
                    deleteVariant(variant);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
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

        eventActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupFormationTable() {
        formationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        formationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        formationDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        formationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        formationActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupPaddleTable() {
        paddleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        paddleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        paddleDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        paddleStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        paddleActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupPartyingTable() {
        partyingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        partyingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        partyingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        partyingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        partyingActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupTeamBuildingTable() {
        teamBuildingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamBuildingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        teamBuildingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        teamBuildingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        teamBuildingActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupAnniversaryTable() {
        anniversaryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        anniversaryDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        anniversaryLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        anniversaryCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        anniversaryPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        anniversaryStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        anniversaryActionColumn.setCellFactory(param -> new TableCell<EventInstance, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    editEvent(eventInstance);
                });
                deleteButton.setOnAction(event -> {
                    EventInstance eventInstance = getTableView().getItems().get(getIndex());
                    deleteEvent(eventInstance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(editButton, deleteButton);
                    setGraphic(hbox);
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

    private void loadCategories() {
        try {
            categoriesTable.setItems(FXCollections.observableArrayList(categoryService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSubcategories() {
        try {
            subcategoriesTable.setItems(FXCollections.observableArrayList(subcategoryService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTypes() {
        try {
            typesTable.setItems(FXCollections.observableArrayList(typeService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadVariants() {
        try {
            variantsTable.setItems(FXCollections.observableArrayList(variantService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEvents() {
        try {
            eventsTable.setItems(FXCollections.observableArrayList(eventService.readAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFormationEvents() {
        try {
            formationTable.setItems(FXCollections.observableArrayList(eventService.findByType("Formation")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPaddleEvents() {
        try {
            paddleTable.setItems(FXCollections.observableArrayList(eventService.findByType("Paddle")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPartyingEvents() {
        try {
            partyingTable.setItems(FXCollections.observableArrayList(eventService.findByType("Partying")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTeamBuildingEvents() {
        try {
            teamBuildingTable.setItems(FXCollections.observableArrayList(eventService.findByType("TeamBuilding")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAnniversaryEvents() {
        try {
            anniversaryTable.setItems(FXCollections.observableArrayList(eventService.findByType("Anniversary")));
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

    @FXML
    private void createCategory() {
        // Open dialog to create category
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Category");
        dialog.setHeaderText("Enter category details");
        dialog.setContentText("Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                TextInputDialog descDialog = new TextInputDialog();
                descDialog.setTitle("Create Category");
                descDialog.setHeaderText("Description:");
                descDialog.showAndWait().ifPresent(description -> {
                    try {
                        EventCategory category = new EventCategory(name.trim(), description.trim());
                        categoryService.ajouter(category);
                        loadCategories();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void editCategory(EventCategory category) {
        TextInputDialog dialog = new TextInputDialog(category.getName());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Enter new name:");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                TextInputDialog descDialog = new TextInputDialog(category.getDescription());
                descDialog.setTitle("Edit Category");
                descDialog.setHeaderText("Description:");
                descDialog.showAndWait().ifPresent(description -> {
                    try {
                        category.setName(name.trim());
                        category.setDescription(description.trim());
                        categoryService.modifier(category);
                        loadCategories();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void deleteCategory(EventCategory category) {
        try {
            categoryService.supprimer(category);
            loadCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createSubcategory() {
        // Select category first
        ChoiceDialog<EventCategory> categoryDialog = new ChoiceDialog<>();
        try {
            categoryDialog.getItems().addAll(categoryService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        categoryDialog.setTitle("Select Category");
        categoryDialog.setHeaderText("Choose a category for the subcategory:");
        categoryDialog.showAndWait().ifPresent(category -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Subcategory");
            dialog.setHeaderText("Enter subcategory details");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog();
                    descDialog.setTitle("Create Subcategory");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            EventSubcategory subcategory = new EventSubcategory(name.trim(), description.trim(), category);
                            subcategoryService.ajouter(subcategory);
                            loadSubcategories();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void editSubcategory(EventSubcategory subcategory) {
        // Similar to create, but edit
        ChoiceDialog<EventCategory> categoryDialog = new ChoiceDialog<>(subcategory.getCategory());
        try {
            categoryDialog.getItems().addAll(categoryService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        categoryDialog.setTitle("Select Category");
        categoryDialog.setHeaderText("Choose a category for the subcategory:");
        categoryDialog.showAndWait().ifPresent(category -> {
            TextInputDialog dialog = new TextInputDialog(subcategory.getName());
            dialog.setTitle("Edit Subcategory");
            dialog.setHeaderText("Enter new name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog(subcategory.getDescription());
                    descDialog.setTitle("Edit Subcategory");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            subcategory.setName(name.trim());
                            subcategory.setDescription(description.trim());
                            subcategory.setCategory(category);
                            subcategoryService.modifier(subcategory);
                            loadSubcategories();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void deleteSubcategory(EventSubcategory subcategory) {
        try {
            subcategoryService.supprimer(subcategory);
            loadSubcategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createType() {
        // Select subcategory
        ChoiceDialog<EventSubcategory> subcategoryDialog = new ChoiceDialog<>();
        try {
            subcategoryDialog.getItems().addAll(subcategoryService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        subcategoryDialog.setTitle("Select Subcategory");
        subcategoryDialog.setHeaderText("Choose a subcategory for the type:");
        subcategoryDialog.showAndWait().ifPresent(subcategory -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Type");
            dialog.setHeaderText("Enter type details");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog();
                    descDialog.setTitle("Create Type");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            EventType type = new EventType(name.trim(), description.trim(), subcategory);
                            typeService.ajouter(type);
                            loadTypes();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void editType(EventType type) {
        ChoiceDialog<EventSubcategory> subcategoryDialog = new ChoiceDialog<>(type.getSubcategory());
        try {
            subcategoryDialog.getItems().addAll(subcategoryService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        subcategoryDialog.setTitle("Select Subcategory");
        subcategoryDialog.setHeaderText("Choose a subcategory for the type:");
        subcategoryDialog.showAndWait().ifPresent(subcategory -> {
            TextInputDialog dialog = new TextInputDialog(type.getName());
            dialog.setTitle("Edit Type");
            dialog.setHeaderText("Enter new name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog(type.getDescription());
                    descDialog.setTitle("Edit Type");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            type.setName(name.trim());
                            type.setDescription(description.trim());
                            type.setSubcategory(subcategory);
                            typeService.modifier(type);
                            loadTypes();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void deleteType(EventType type) {
        try {
            typeService.supprimer(type);
            loadTypes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createVariant() {
        // Select type
        ChoiceDialog<EventType> typeDialog = new ChoiceDialog<>();
        try {
            typeDialog.getItems().addAll(typeService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        typeDialog.setTitle("Select Type");
        typeDialog.setHeaderText("Choose a type for the variant:");
        typeDialog.showAndWait().ifPresent(type -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Variant");
            dialog.setHeaderText("Enter variant details");
            dialog.setContentText("Name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog();
                    descDialog.setTitle("Create Variant");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            EventVariant variant = new EventVariant(name.trim(), description.trim(), type);
                            variantService.ajouter(variant);
                            loadVariants();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void editVariant(EventVariant variant) {
        ChoiceDialog<EventType> typeDialog = new ChoiceDialog<>(variant.getType());
        try {
            typeDialog.getItems().addAll(typeService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        typeDialog.setTitle("Select Type");
        typeDialog.setHeaderText("Choose a type for the variant:");
        typeDialog.showAndWait().ifPresent(type -> {
            TextInputDialog dialog = new TextInputDialog(variant.getName());
            dialog.setTitle("Edit Variant");
            dialog.setHeaderText("Enter new name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    TextInputDialog descDialog = new TextInputDialog(variant.getDescription());
                    descDialog.setTitle("Edit Variant");
                    descDialog.setHeaderText("Description:");
                    descDialog.showAndWait().ifPresent(description -> {
                        try {
                            variant.setName(name.trim());
                            variant.setDescription(description.trim());
                            variant.setType(type);
                            variantService.modifier(variant);
                            loadVariants();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    private void deleteVariant(EventVariant variant) {
        try {
            variantService.supprimer(variant);
            loadVariants();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createEvent() {
        // Select variant
        ChoiceDialog<EventVariant> variantDialog = new ChoiceDialog<>();
        try {
            variantDialog.getItems().addAll(variantService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        variantDialog.setTitle("Select Variant");
        variantDialog.setHeaderText("Choose a variant for the event:");
        variantDialog.showAndWait().ifPresent(variant -> {
            // For simplicity, use text inputs for other fields. In a real app, use a proper form.
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Create Event");
            nameDialog.setHeaderText("Event Name:");
            nameDialog.showAndWait().ifPresent(name -> {
                // Add more dialogs for date, etc. For now, basic implementation.
                try {
                    EventInstance event = new EventInstance(name.trim(), LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2), "Default Location", 100, 0.0, "Admin", "Description", "draft", variant);
                    eventService.ajouter(event);
                    loadEvents();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void editEvent(EventInstance event) {
        // Similar to create, but edit
        ChoiceDialog<EventVariant> variantDialog = new ChoiceDialog<>(event.getVariant());
        try {
            variantDialog.getItems().addAll(variantService.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        variantDialog.setTitle("Select Variant");
        variantDialog.setHeaderText("Choose a variant for the event:");
        variantDialog.showAndWait().ifPresent(variant -> {
            TextInputDialog nameDialog = new TextInputDialog(event.getName());
            nameDialog.setTitle("Edit Event");
            nameDialog.setHeaderText("Event Name:");
            nameDialog.showAndWait().ifPresent(name -> {
                try {
                    event.setName(name.trim());
                    event.setVariant(variant);
                    eventService.modifier(event);
                    loadEvents();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void deleteEvent(EventInstance event) {
        try {
            eventService.supprimer(event);
            loadEvents();
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