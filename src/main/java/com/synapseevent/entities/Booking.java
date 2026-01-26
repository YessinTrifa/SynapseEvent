package com.synapseevent.entities;

import java.time.LocalDate;

public class Booking {
    private Long id;
    private User user;
    private String eventType;
    private Long eventId;
    private LocalDate bookingDate;
    private String status;

    // Constructors
    public Booking() {}

    public Booking(User user, String eventType, Long eventId, LocalDate bookingDate, String status) {
        this.user = user;
        this.eventType = eventType;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public Booking(Long id, User user, String eventType, Long eventId, LocalDate bookingDate, String status) {
        this.id = id;
        this.user = user;
        this.eventType = eventType;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + user +
                ", eventType='" + eventType + '\'' +
                ", eventId=" + eventId +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                '}';
    }
}