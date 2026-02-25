package com.synapseevent.entities;

import java.time.LocalDate;

public class CustomEventRequest {

    private Long id;

    private Long userId;
    private User user;

    private String eventType;
    private LocalDate eventDate;

    private String description;

    private String status = "pending";
    private LocalDate createdDate = LocalDate.now();

    private Double budget;
    private Integer capacity;
    private String location;

    private String reason;

    public CustomEventRequest() {}

    public CustomEventRequest(Long userId, String eventType, LocalDate eventDate, String description,
                              String status, LocalDate createdDate, Double budget, Integer capacity,
                              String location, String reason) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        setStatus(status);
        setCreatedDate(createdDate);
        this.budget = budget;
        this.capacity = capacity;
        this.location = location;
        this.reason = reason;
    }

    public CustomEventRequest(Long id, Long userId, String eventType, LocalDate eventDate, String description,
                              String status, LocalDate createdDate, Double budget, Integer capacity,
                              String location, String reason) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        setStatus(status);
        setCreatedDate(createdDate);
        this.budget = budget;
        this.capacity = capacity;
        this.location = location;
        this.reason = reason;
    }

    public CustomEventRequest(Long id, User user, String eventType, LocalDate eventDate, String description,
                              String status, LocalDate createdDate, Double budget, Integer capacity,
                              String location) {
        this.id = id;
        setUser(user);
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.description = description;
        setStatus(status);
        setCreatedDate(createdDate);
        this.budget = budget;
        this.capacity = capacity;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) this.userId = user.getId();
    }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) this.status = "pending";
        else this.status = status;
    }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) {
        if (createdDate == null) this.createdDate = LocalDate.now();
        else this.createdDate = createdDate;
    }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    @Override
    public String toString() {
        return "CustomEventRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventType='" + eventType + '\'' +
                ", eventDate=" + eventDate +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", budget=" + budget +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
