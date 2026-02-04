package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class PaddleEvent {
    private Long id;
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Integer capacity;
    private Double price;
    private String organizer;
    private String description;
    private String status = "draft";
    private String type = "Paddle";

    public PaddleEvent() {}

    public PaddleEvent(String name, LocalDate date, String description, String status) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public PaddleEvent(Long id, String name, LocalDate date, String description, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public PaddleEvent(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                       String location, Integer capacity, Double price, String organizer,
                       String description, String status) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.organizer = organizer;
        this.description = description;
        this.status = status;
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

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() { return name; }
}
