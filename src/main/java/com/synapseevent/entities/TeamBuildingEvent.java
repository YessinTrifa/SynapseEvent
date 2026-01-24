package com.synapseevent.entities;

import java.time.LocalDate;

public class TeamBuildingEvent {
    private Long id;
    private String name;
    private LocalDate date;
    private String description;

    // Constructors
    public TeamBuildingEvent() {}
    public TeamBuildingEvent(String name, LocalDate date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public TeamBuildingEvent(Long id, String name, LocalDate date, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return name; }
}