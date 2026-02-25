package com.synapseevent.entities;

import java.time.LocalDate;

public class Booking {

    private Long id;

    private Long userId;
    private User user;

    private String eventType;
    private Long eventId;

    private LocalDate bookingDate;

    private String status = "pending";

    public Booking() {}

    public Booking(Long userId, String eventType, Long eventId, LocalDate bookingDate, String status) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        setStatus(status);
    }

    public Booking(Long id, Long userId, String eventType, Long eventId, LocalDate bookingDate, String status) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        setStatus(status);
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

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) this.status = "pending";
        else this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventType='" + eventType + '\'' +
                ", eventId=" + eventId +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                '}';
    }
}