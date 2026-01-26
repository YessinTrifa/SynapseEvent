package com.synapseevent.entities;

import java.time.LocalDate;

public class EventSummary {
    private String type;
    private Long id;
    private String name;
    private LocalDate date;
    private String description;

    public EventSummary(String type, Long id, String name, LocalDate date, String description) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    // Getters
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
}