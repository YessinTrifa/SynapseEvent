package com.synapseevent.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "userId")
    private Long userId;
    
    @Column(name = "eventType")
    private String eventType;
    
    @Column(name = "eventId")
    private Long eventId;
    
    @Column(name = "rating")
    private Integer rating; // 1-5 stars
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    // Constructors
    public Review() {}

    public Review(Long userId, String eventType, Long eventId, Integer rating, String comment) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public Review(Long id, Long userId, String eventType, Long eventId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

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