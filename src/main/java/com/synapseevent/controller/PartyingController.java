package com.synapseevent.controller;

import com.synapseevent.entities.PartyingEvent;
import com.synapseevent.entities.Venue;
import com.synapseevent.entities.EventInstance;
import com.synapseevent.service.PartyingEventService;
import com.synapseevent.service.VenueService;
import com.synapseevent.service.EventInstanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.synapseevent.entities.EventTemplate;
import java.time.LocalTime;
import javafx.scene.control.SpinnerValueFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PartyingController implements TemplateAware{
    @FXML private TableView<PartyingEvent> partyingTable;
    @FXML private TableColumn<PartyingEvent, Long> idColumn;
    @FXML private TableColumn<PartyingEvent, String> nameColumn;
    @FXML private TableColumn<PartyingEvent, String> dateColumn;
    @FXML private TableColumn<PartyingEvent, String> venueColumn;
    @FXML private TableColumn<PartyingEvent, String> capacityColumn;
    @FXML private TableColumn<PartyingEvent, String> priceColumn;
    @FXML private TableColumn<PartyingEvent, String> statusColumn;

    @FXML private TextField nameField;
    @FXML private DatePicker datePicker;
    @FXML private DatePicker filterDatePicker;
    @FXML private ComboBox<String> startTimeComboBox;
    @FXML private ComboBox<String> endTimeComboBox;
    @FXML private ComboBox<Venue> venueComboBox;
    @FXML private ComboBox<String> venueTypeFilterComboBox;
    @FXML private TextField capacityField;
    @FXML private TextField priceField;
    @FXML private TextField ageRestrictionField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionField;
    
    // New fields
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> musicTypeComboBox;

    private PartyingEventService partyingEventService = new PartyingEventService();
    private VenueService venueService = new VenueService();
    private EventInstanceService eventInstanceService = new EventInstanceService();

    @FXML
    public void initialize() {
        // Initialize time combo boxes with hours
        ObservableList<String> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i));
        }
        startTimeComboBox.setItems(hours);
        startTimeComboBox.setValue("18:00");
        endTimeComboBox.setItems(hours);
        endTimeComboBox.setValue("23:00");

        // Initialize theme options
        ObservableList<String> themes = FXCollections.observableArrayList(
            "🎉 Birthday Party",
            "🎊 New Year's Eve",
            "🎄 Holiday Celebration",
            "💃 Latin Night",
            "🕺 80s/90s Retro",
            "🎵 Open Mic Night",
            "👔 Corporate Gala",
            "🎓 Graduation Party",
            "💍 Bachelor/Bachelorette",
            "🌴 Summer Bash",
            "🎃 Halloween",
            "💜 Ladies Night",
            "🤵 Gentlemen's Night",
            "🎤 Karaoke Night",
            "🔥 Bonfire Party"
        );
        themeComboBox.setItems(themes);

        // Initialize music type options
        ObservableList<String> musicTypes = FXCollections.observableArrayList(
            "🎵 Pop",
            "🎸 Rock",
            "💃 Hip-Hop/R&B",
            "🎹 Electronic/EDM",
            "🎺 Jazz/Swing",
            "🌴 Latin",
            "🎻 Classical",
            "🔥 Afrobeat",
            "🎤 Mixed/Variety",
            "🎧 DJ Night"
        );
        musicTypeComboBox.setItems(musicTypes);

        // Set default values for fields
        capacityField.setText("50");
        priceField.setText("0");
        ageRestrictionField.setText("18");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getDate() != null ? cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : ""));
        venueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getVenue() != null ? cellData.getValue().getVenue().toString() : ""));
        capacityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getCapacity() != null ? cellData.getValue().getCapacity().toString() : ""));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getPrice() != null ? String.format("%.2f TND", cellData.getValue().getPrice()) : ""));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set status ComboBox items
        ObservableList<String> statuses = FXCollections.observableArrayList("draft", "published", "cancelled");
        statusComboBox.setItems(statuses);
        statusComboBox.setValue("draft");

        // Set venue type filter items
        ObservableList<String> venueTypes = FXCollections.observableArrayList("All", "CLUB", "BEACH", "HOTEL");
        venueTypeFilterComboBox.setItems(venueTypes);
        venueTypeFilterComboBox.setValue("All");

        loadVenues();
        loadData();
    }

    private void loadVenues() {
        try {
            venueComboBox.setItems(FXCollections.observableArrayList(venueService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            partyingTable.setItems(FXCollections.observableArrayList(partyingEventService.readAll()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterByDate() {
        LocalDate filterDate = filterDatePicker.getValue();
        try {
            if (filterDate != null) {
                partyingTable.setItems(FXCollections.observableArrayList(
                    partyingEventService.getEventsByDate(filterDate)));
            } else {
                loadData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterByVenueType() {
        String type = venueTypeFilterComboBox.getValue();
        try {
            ObservableList<PartyingEvent> events = FXCollections.observableArrayList();
            
            if ("All".equals(type)) {
                events.addAll(partyingEventService.readAll());
            } else {
                // Filter events by venue type
                for (PartyingEvent event : partyingEventService.readAll()) {
                    if (event.getVenue() != null && event.getVenue().getType().equals(type)) {
                        events.add(event);
                    }
                }
            }
            partyingTable.setItems(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Integer parseTimeToHour(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return 0;
        try {
            return Integer.parseInt(timeStr.split(":")[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    @FXML
    private void addPartyingEvent() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime startTime = LocalTime.of(parseTimeToHour(startTimeComboBox.getValue()), 0);
        LocalTime endTime = LocalTime.of(parseTimeToHour(endTimeComboBox.getValue()), 0);
        Venue selectedVenue = venueComboBox.getValue();
        Long venueId = selectedVenue != null ? selectedVenue.getId() : null;
        
        Integer capacity = parseCapacity(capacityField.getText());
        Double price = parsePrice(priceField.getText());
        Integer ageRestriction = parseAgeRestriction(ageRestrictionField.getText());
        
        String description = descriptionField.getText();
        String status = statusComboBox.getValue() != null ? statusComboBox.getValue() : "draft";
        String theme = themeComboBox.getValue();
        String musicType = musicTypeComboBox.getValue();

        if (name != null && !name.isEmpty() && date != null) {
            PartyingEvent event = new PartyingEvent(name, date, startTime, endTime,
                venueId, capacity, price, "admin@synapse.com", description, status);
            event.setTheme(theme);
            event.setMusicType(musicType);
            event.setAgeRestriction(ageRestriction);
            
            try {
                // Save to PartyingEvent table
                partyingEventService.ajouter(event);
                
                // Also save to event_instance table for admin dashboard
                EventInstance eventInstance = new EventInstance(
                    name, date, startTime, endTime,
                    selectedVenue != null ? selectedVenue.getName() : "",
                    capacity, price, "admin@synapse.com", description, status, "Partying");
                eventInstanceService.ajouter(eventInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
            clearFields();
            loadData();
        }
    }

    @FXML
    private void updatePartyingEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setDate(datePicker.getValue());
            selected.setStartTime(LocalTime.of(parseTimeToHour(startTimeComboBox.getValue()), 0));
            selected.setEndTime(LocalTime.of(parseTimeToHour(endTimeComboBox.getValue()), 0));
            Venue selectedVenue = venueComboBox.getValue();
            selected.setVenueId(selectedVenue != null ? selectedVenue.getId() : null);
            selected.setCapacity(parseCapacity(capacityField.getText()));
            selected.setPrice(parsePrice(priceField.getText()));
            selected.setDescription(descriptionField.getText());
            selected.setTheme(themeComboBox.getValue());
            selected.setMusicType(musicTypeComboBox.getValue());
            selected.setAgeRestriction(parseAgeRestriction(ageRestrictionField.getText()));
            if (statusComboBox.getValue() != null) {
                selected.setStatus(statusComboBox.getValue());
            }

            try {
                // Update PartyingEvent table
                partyingEventService.modifier(selected);
                
                // Also update event_instance table for admin dashboard
                EventInstance eventInstance = new EventInstance(
                    selected.getName(), selected.getDate(), selected.getStartTime(), selected.getEndTime(),
                    selectedVenue != null ? selectedVenue.getName() : "",
                    selected.getCapacity(), selected.getPrice(), selected.getOrganizer(), 
                    selected.getDescription(), selected.getStatus(), "Partying");
                eventInstance.setId(selected.getId());
                eventInstanceService.modifier(eventInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void deletePartyingEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                // Delete from event_instance table first (has FK)
                EventInstance eventInstance = new EventInstance();
                eventInstance.setId(selected.getId());
                eventInstanceService.supprimer(eventInstance);
                
                // Delete from PartyingEvent table
                partyingEventService.supprimer(selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadData();
        }
    }

    @FXML
    private void publishEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("published");
            try {
                // Update PartyingEvent
                partyingEventService.modifier(selected);
                
                // Also update event_instance
                EventInstance eventInstance = new EventInstance(
                    selected.getName(), selected.getDate(), selected.getStartTime(), selected.getEndTime(),
                    selected.getVenue() != null ? selected.getVenue().getName() : "",
                    selected.getCapacity(), selected.getPrice(), selected.getOrganizer(), 
                    selected.getDescription(), selected.getStatus(), "Partying");
                eventInstance.setId(selected.getId());
                eventInstanceService.modifier(eventInstance);
                
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveAsDraft() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("draft");
            try {
                partyingEventService.modifier(selected);
                
                EventInstance eventInstance = new EventInstance(
                    selected.getName(), selected.getDate(), selected.getStartTime(), selected.getEndTime(),
                    selected.getVenue() != null ? selected.getVenue().getName() : "",
                    selected.getCapacity(), selected.getPrice(), selected.getOrganizer(), 
                    selected.getDescription(), selected.getStatus(), "Partying");
                eventInstance.setId(selected.getId());
                eventInstanceService.modifier(eventInstance);
                
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void selectPartyingEvent() {
        PartyingEvent selected = partyingTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            datePicker.setValue(selected.getDate());

            if (selected.getStartTime() != null) {
                startTimeComboBox.setValue(String.format("%02d:00", selected.getStartTime().getHour()));
            }
            if (selected.getEndTime() != null) {
                endTimeComboBox.setValue(String.format("%02d:00", selected.getEndTime().getHour()));
            }

            if (selected.getVenue() != null) {
                venueComboBox.setValue(selected.getVenue());
            }
            if (selected.getCapacity() != null) {
                capacityField.setText(selected.getCapacity().toString());
            }
            if (selected.getPrice() != null) {
                priceField.setText(selected.getPrice().toString());
            }
            if (selected.getAgeRestriction() != null) {
                ageRestrictionField.setText(selected.getAgeRestriction().toString());
            }
            statusComboBox.setValue(selected.getStatus());
            descriptionField.setText(selected.getDescription());
            themeComboBox.setValue(selected.getTheme());
            musicTypeComboBox.setValue(selected.getMusicType());
        }
    }

    @FXML
    private void clearFields() {
        nameField.clear();
        datePicker.setValue(null);
        startTimeComboBox.setValue("18:00");
        endTimeComboBox.setValue("23:00");
        venueComboBox.setValue(null);
        capacityField.setText("50");
        priceField.setText("0");
        ageRestrictionField.setText("18");
        statusComboBox.setValue("draft");
        descriptionField.clear();
        themeComboBox.setValue(null);
        musicTypeComboBox.setValue(null);
    }
    
    private Integer parseCapacity(String value) {
        try {
            return value != null && !value.isEmpty() ? Integer.parseInt(value) : 50;
        } catch (NumberFormatException e) {
            return 50;
        }
    }
    
    private Double parsePrice(String value) {
        try {
            return value != null && !value.isEmpty() ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private Integer parseAgeRestriction(String value) {
        try {
            return value != null && !value.isEmpty() ? Integer.parseInt(value) : 18;
        } catch (NumberFormatException e) {
            return 18;
        }
    }

    @Override
    public void applyTemplate(EventTemplate t) {
        if (t == null) return;

        // Time -> your UI uses combo boxes
        LocalTime st = t.getDefaultStartTime();
        LocalTime et = t.getDefaultEndTime();

        if (st != null && startTimeComboBox != null) {
            startTimeComboBox.setValue(String.format("%02d:00", st.getHour()));
        }
        if (et != null && endTimeComboBox != null) {
            endTimeComboBox.setValue(String.format("%02d:00", et.getHour()));
        }

        if (t.getDefaultCapacity() != null && capacityField != null) {
            capacityField.setText(t.getDefaultCapacity().toString());
        }

        if (t.getDefaultPrice() != null && priceField != null) {
            priceField.setText(t.getDefaultPrice().toString());
        }

        if (t.getDefaultDescription() != null) {
            descriptionField.setText(t.getDefaultDescription());
        }
    }
}
