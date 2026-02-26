package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class PartyingEvent {

    private Long id;

    private String name;
    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    private Long venueId;
    private Venue venue;

    private Integer capacity;
    private Double price;

    private String organizer;
    private String description;

    private String status = "draft";
    private String theme;
    private String musicType;
    private Integer ageRestriction = 18;

    private final String type = "Partying";

    public PartyingEvent() {}

    public PartyingEvent(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                         Long venueId, Integer capacity, Double price, String organizer,
                         String description, String status) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venueId = venueId;
        this.capacity = capacity;
        this.price = price;
        this.organizer = organizer;
        this.description = description;
        setStatus(status);
    }

    public PartyingEvent(Long id, String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                         Long venueId, Integer capacity, Double price, String organizer,
                         String description, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venueId = venueId;
        this.capacity = capacity;
        this.price = price;
        this.organizer = organizer;
        this.description = description;
        setStatus(status);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }

    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) {
        this.venue = venue;
        if (venue != null) this.venueId = venue.getId();
    }

    public String getLocation() {
        return venue != null ? venue.getName() : "";
    }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) this.status = "draft";
        else this.status = status;
    }

    public String getType() { return type; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getMusicType() { return musicType; }
    public void setMusicType(String musicType) { this.musicType = musicType; }

    public Integer getAgeRestriction() { return ageRestriction; }
    public void setAgeRestriction(Integer ageRestriction) { this.ageRestriction = ageRestriction; }

    @Override
    public String toString() { return name; }
}