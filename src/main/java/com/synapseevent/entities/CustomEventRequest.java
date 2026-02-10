package com.synapseevent.entities;

import java.time.LocalDate;

public class CustomEventRequest {
    private Long id;
    private User user;
    private String eventType;
    private LocalDate eventDate;
    private String description;
    private String status;
    private LocalDate createdDate;
    private Double budget;
    private Integer capacity;
    private String location;

    // Constructors
    public CustomEventRequest() {}

    public CustomEventRequest(User user, String eventType, LocalDate eventDate, String description, String status, LocalDate createdDate) {
        this.user = user;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    public CustomEventRequest(Long id, User user, String eventType, LocalDate eventDate, String description, String status, LocalDate createdDate) {
        this.id = id;
        this.user = user;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    // New constructor with budget, capacity, and location
    public CustomEventRequest(User user, String eventType, LocalDate eventDate, String description, 
                              String status, LocalDate createdDate, Double budget, Integer capacity, String location) {
        this.user = user;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.budget = budget;
        this.capacity = capacity;
        this.location = location;
    }

    public CustomEventRequest(Long id, User user, String eventType, LocalDate eventDate, String description, 
                              String status, LocalDate createdDate, Double budget, Integer capacity, String location) {
        this.id = id;
        this.user = user;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.budget = budget;
        this.capacity = capacity;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return "CustomEventRequest{" +
                "id=" + id +
                ", user=" + user +
                ", eventType='" + eventType + '\'' +
                ", eventDate=" + eventDate +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", budget=" + budget +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                '}';
    }
}
