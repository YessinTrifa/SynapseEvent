package com.synapseevent.controller;

import com.synapseevent.entities.EventInstance;
import com.synapseevent.entities.TeamBuildingEvent;
import com.synapseevent.entities.TeamBuildingActivity;
import com.synapseevent.entities.Venue;
import com.synapseevent.service.EventInstanceService;
import com.synapseevent.service.TeamBuildingActivityService;
import com.synapseevent.service.TeamBuildingEventService;
import com.synapseevent.service.VenueService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import com.synapseevent.entities.EventTemplate;
import java.time.LocalTime;
import javafx.scene.control.SpinnerValueFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class TeamBuildingController implements TemplateAware{
    @FXML private TableView<TeamBuildingEvent> teamBuildingTable;
    @FXML private TableColumn<TeamBuildingEvent, Long> idColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> nameColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> dateColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> locationColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> capacityColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> priceColumn;
    @FXML private TableColumn<TeamBuildingEvent, String> statusColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> startTimeHourSpinner;
    @FXML private Spinner<Integer> endTimeHourSpinner;
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private Spinner<Integer> capacitySpinner;
    @FXML private Spinner<Double> priceSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<TeamBuildingActivity> activitiesComboBox;
    @FXML private Label selectedActivitiesLabel;
    @FXML private CheckBox isPackCheckBox;
    
    // Track selected activities
    private List<TeamBuildingActivity> selectedActivitiesList = new ArrayList<>();

    private TeamBuildingEventService teamBuildingEventService = new TeamBuildingEventService();
    private TeamBuildingActivityService activityService = new TeamBuildingActivityService();
    private VenueService venueService = new VenueService();
    private EventInstanceService eventInstanceService = new EventInstanceService();

    @FXML
    public void initialize() {
        startTimeHourSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9)
        );
        endTimeHourSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12)
        );
        capacitySpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 20)
        );
        priceSpinner.setValueFactory(
                new javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100000, 0, 1)
        );
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDate() != null ? cellData.getValue().getDate().toString() : ""));
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getLocation() != null ? cellData.getValue().getLocation() : ""));
        capacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCapacity() != null ? cellData.getValue().getCapacity().toString() : ""));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getPrice() != null ? cellData.getValue().getPrice().toString() + " TND" : ""));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set status ComboBox items programmatically
        ObservableList<String> statuses = FXCollections.observableArrayList("draft", "published", "cancelled");
        statusComboBox.setItems(statuses);
        statusComboBox.setValue("draft");

        // Set venue type filter items
        ObservableList<String> venueTypes = FXCollections.observableArrayList("All", "CLUB", "BEACH", "HOTEL", "RESTAURANT");
        venueTypeFilterComboBox.setItems(venueTypes);
        venueTypeFilterComboBox.setValue("All");

        // Load venues and set up filter listener
        loadVenues();
        venueTypeFilterComboBox.setOnAction(e -> filterVenuesByType());
        
        // Load activities for packs
        loadActivities();

        loadData();
    }
    
    private void loadActivities() {
        try {
            if (activitiesComboBox != null) {
                activitiesComboBox.getItems().addAll(activityService.readAll());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void addActivityToEvent() {
        TeamBuildingActivity selected = activitiesComboBox.getValue();
        if (selected != null) {
            // Check if already added
            boolean alreadyAdded = false;
            for (TeamBuildingActivity a : selectedActivitiesList) {
                if (a.getId().equals(selected.getId())) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                selectedActivitiesList.add(selected);
                updateSelectedActivitiesLabel();
            }
        }
    }
    
    private void updateSelectedActivitiesLabel() {
        if (selectedActivitiesLabel != null) {
            if (selectedActivitiesList.isEmpty()) {
                selectedActivitiesLabel.setText("Selected: None");
            } else {
                StringBuilder sb = new StringBuilder("Selected: ");
                for (int i = 0; i < selectedActivitiesList.size(); i++) {
                    sb.append(selectedActivitiesList.get(i).getName());
                    if (i < selectedActivitiesList.size() - 1) {
                        sb.append(", ");
                    }
                }
                selectedActivitiesLabel.setText(sb.toString());
            }
        }
    }

    private void loadVenues() {
        try {
            venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterVenuesByType() {
        String selectedType = venueTypeFilterComboBox.getValue();
        try {
            if ("All".equals(selectedType)) {
                venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
            } else {
                venueComboBox.setItems(FXCollections.observableArrayList(venueService.findByType(selectedType)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            teamBuildingTable.setItems(FXCollections.observableArrayList(teamBuildingEventService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addTeamBuildingEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = LocalTime.of(startTimeHourSpinner.getValue(), 0);
        LocalTime endTime = LocalTime.of(endTimeHourSpinner.getValue(), 0);
        Venue selectedVenue = venueComboBox.getValue();
        String location = selectedVenue != null ? selectedVenue.getName() : "";
        Integer capacity = capacitySpinner.getValue();
        Double price = priceSpinner.getValue();
        String description = descriptionField.getText();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";
        Boolean isPack = isPackCheckBox.isSelected();
        StringBuilder activitiesBuilder = new StringBuilder();
        // Use selectedActivitiesList if available, otherwise fall back to ComboBox
        if (!selectedActivitiesList.isEmpty()) {
            for (int i = 0; i < selectedActivitiesList.size(); i++) {
                activitiesBuilder.append(selectedActivitiesList.get(i).getName());
                if (i < selectedActivitiesList.size() - 1) {
                    activitiesBuilder.append(", ");
                }
            }
        } else if (isPack && activitiesComboBox.getValue() != null) {
            // Single activity selected from dropdown
            TeamBuildingActivity selectedActivity = activitiesComboBox.getValue();
            activitiesBuilder.append(selectedActivity.getName());
        }
        String activities = activitiesBuilder.toString();

        if (name != null && !name.isEmpty() && date != null) {
            TeamBuildingEvent event = new TeamBuildingEvent(name, date, startTime, endTime,
                location, capacity, price, "admin@synapse.com", description, status);
            event.setIsPack(isPack);
            event.setActivities(activities);
            
            // Calculate price for packs based on activities
            if (isPack && !selectedActivitiesList.isEmpty()) {
                Double calculatedPrice = calculatePackPriceFromList(selectedActivitiesList, capacity);
                event.setPrice(calculatedPrice);
            }
            
            try {
                teamBuildingEventService.ajouter(event);
                EventInstance ei = new EventInstance(
                        event.getId(),
                        name, date, startTime, endTime,
                        location, capacity, event.getPrice(),
                        "admin@synapse.com", description, status, "TeamBuilding"
                );
                // Include pack info in description
                if (isPack) {
                    ei.setDescription(description + "\n\n[PACK] Activities: " + activities);
                }
                eventInstanceService.ajouter(ei);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFields();
            loadData();
        }
    }
    
    private Double calculatePackPrice(String activities, Integer capacity, String venue) {
        // Base prices
        double basePrice = 0;
        
        // Add venue cost
        if (venue != null) {
            switch (venue.toLowerCase()) {
                case "hotel":
                case "resort":
                    basePrice += 500;
                    break;
                case "restaurant":
                    basePrice += 300;
                    break;
                default:
                    basePrice += 200;
            }
        }
        
        // Calculate activity costs per person
        String[] activityList = activities.split(",");
        double activityCostPerPerson = 0;
        
        for (String activity : activityList) {
            String act = activity.trim().toLowerCase();
            if (act.contains("escape")) activityCostPerPerson += 25;
            else if (act.contains("laser")) activityCostPerPerson += 30;
            else if (act.contains("bowling")) activityCostPerPerson += 20;
            else if (act.contains("kart")) activityCostPerPerson += 35;
            else if (act.contains("paint")) activityCostPerPerson += 40;
            else if (act.contains("cooking")) activityCostPerPerson += 50;
            else if (act.contains("wine")) activityCostPerPerson += 45;
            else if (act.contains("camp")) activityCostPerPerson += 80;
            else if (act.contains("raft")) activityCostPerPerson += 60;
            else activityCostPerPerson += 15; // default activity price
        }
        
        // Total = base venue cost + (activity cost per person × number of people)
        int participants = capacity != null ? capacity : 10;
        return basePrice + (activityCostPerPerson * participants);
    }
    
    private Double calculatePackPriceFromList(List<TeamBuildingActivity> activities, Integer capacity) {
        double totalActivityCost = 0;
        
        // Sum up all activity prices from the list
        for (TeamBuildingActivity activity : activities) {
            totalActivityCost += activity.getPricePerPerson() != null ? activity.getPricePerPerson() : 0;
        }
        
        // Total = sum of activity prices × number of people
        int participants = capacity != null ? capacity : 10;
        return totalActivityCost * participants;
    }

    @FXML
    private void updateTeamBuildingEvent() {
        TeamBuildingEvent selected = teamBuildingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setStartTime(LocalTime.of(startTimeHourSpinner.getValue(), 0));
            selected.setEndTime(LocalTime.of(endTimeHourSpinner.getValue(), 0));
            Venue selectedVenue = venueComboBox.getValue();
            selected.setLocation(selectedVenue != null ? selectedVenue.getName() : "");
            selected.setCapacity(capacitySpinner.getValue());
            selected.setPrice(priceSpinner.getValue());
            selected.setDescription(descriptionField.getText());
            if (statusComboBox.getValue() != null) {
                selected.setStatus(statusComboBox.getValue());
            }

            try {
                teamBuildingEventService.modifier(selected);
                EventInstance ei = eventInstanceService.findbyId(selected.getId());
                if (ei != null) {
                    ei.setName(selected.getName());
                    ei.setDate(selected.getDate());
                    ei.setStartTime(selected.getStartTime());
                    ei.setEndTime(selected.getEndTime());
                    ei.setLocation(selected.getLocation());
                    ei.setCapacity(selected.getCapacity());
                    ei.setPrice(selected.getPrice());
                    ei.setDescription(selected.getDescription());
                    ei.setStatus(selected.getStatus());
                    eventInstanceService.modifier(ei);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void deleteTeamBuildingEvent() {
        TeamBuildingEvent selected = teamBuildingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                EventInstance ei = eventInstanceService.findbyId(selected.getId());
                if (ei != null) eventInstanceService.supprimer(ei);
                teamBuildingEventService.supprimer(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void publishEvent() {
        TeamBuildingEvent selected = teamBuildingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("published");
            try {
                teamBuildingEventService.modifier(selected);
                EventInstance ei = eventInstanceService.findbyId(selected.getId());
                if (ei != null) {
                    ei.setStatus("published");
                    eventInstanceService.modifier(ei);
                }
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAsDraft() {
        TeamBuildingEvent selected = teamBuildingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("draft");
            try {
                teamBuildingEventService.modifier(selected);
                EventInstance ei = eventInstanceService.findbyId(selected.getId());
                if (ei != null) {
                    ei.setStatus("draft");
                    eventInstanceService.modifier(ei);
                }
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void selectTeamBuildingEvent() {
        TeamBuildingEvent selected = teamBuildingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            datePicker.setValue(selected.getDate());

            if (selected.getStartTime() != null) {
                startTimeHourSpinner.getValueFactory().setValue(selected.getStartTime().getHour());
            }
            if (selected.getEndTime() != null) {
                endTimeHourSpinner.getValueFactory().setValue(selected.getEndTime().getHour());
            }

            // Try to find and select the venue in the combo box
            if (selected.getLocation() != null) {
                for (Venue venue : venueComboBox.getItems()) {
                    if (venue.getName().equals(selected.getLocation())) {
                        venueComboBox.setValue(venue);
                        break;
                    }
                }
            }
            if (selected.getCapacity() != null) {
                capacitySpinner.getValueFactory().setValue(selected.getCapacity());
            }
            if (selected.getPrice() != null) {
                priceSpinner.getValueFactory().setValue(selected.getPrice());
            }
            statusComboBox.setValue(selected.getStatus());
            descriptionField.setText(selected.getDescription());
        }
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeHourSpinner.getValueFactory().setValue(9);
        endTimeHourSpinner.getValueFactory().setValue(17);
        venueComboBox.setValue(null);
        venueTypeFilterComboBox.setValue("All");
        capacitySpinner.getValueFactory().setValue(30);
        priceSpinner.getValueFactory().setValue(0.0);
        statusComboBox.setValue("draft");
        descriptionField.clear();
        // Clear selected activities
        selectedActivitiesList.clear();
        activitiesComboBox.setValue(null);
        updateSelectedActivitiesLabel();
    }
    
    @FXML
    private void generateReport() {
        // Show information dialog - Report generation functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Generate Report");
        alert.setHeaderText(null);
        alert.setContentText("Report generation feature is not yet implemented.\nThis would generate a report of all Team Building events.");
        alert.showAndWait();
    }
    
    @Override
    public void applyTemplate(EventTemplate t) {
        if (t == null) return;

        // Time -> your UI uses only HOUR spinners
        LocalTime st = t.getDefaultStartTime();
        LocalTime et = t.getDefaultEndTime();

        if (st != null && startTimeHourSpinner.getValueFactory() != null) {
            startTimeHourSpinner.getValueFactory().setValue(st.getHour());
        }
        if (et != null && endTimeHourSpinner.getValueFactory() != null) {
            endTimeHourSpinner.getValueFactory().setValue(et.getHour());
        }

        if (t.getDefaultCapacity() != null && capacitySpinner.getValueFactory() != null) {
            capacitySpinner.getValueFactory().setValue(t.getDefaultCapacity());
        }

        if (t.getDefaultPrice() != null && priceSpinner.getValueFactory() != null) {
            priceSpinner.getValueFactory().setValue(t.getDefaultPrice());
        }

        if (t.getDefaultDescription() != null) {
            descriptionField.setText(t.getDefaultDescription());
        }
    }
}
