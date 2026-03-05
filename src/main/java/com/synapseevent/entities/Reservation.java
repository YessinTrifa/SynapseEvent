package com.synapseevent.entities;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private Long eventId;
    private Long userId;
    private Integer seats;
    private String status = "CONFIRMED";
    private LocalDateTime createdAt;
    
    public Reservation() {}
    
    public Reservation(Long eventId, Long userId, Integer seats, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.seats = seats;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
