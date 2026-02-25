package com.synapseevent.entities;

import java.time.LocalDateTime;

public class Review {

    private Long id;

    private Long userId;

    private String eventType;
    private Long eventId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {}

    public Review(Long userId, String eventType, Long eventId, Integer rating, String comment) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        setRating(rating);
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public Review(Long id, Long userId, String eventType, Long eventId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        setRating(rating);
        this.comment = comment;
        setCreatedAt(createdAt);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) {
        if (rating == null) this.rating = null;
        else if (rating < 1) this.rating = 1;
        else if (rating > 5) this.rating = 5;
        else this.rating = rating;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) this.createdAt = LocalDateTime.now();
        else this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventType='" + eventType + '\'' +
                ", eventId=" + eventId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}