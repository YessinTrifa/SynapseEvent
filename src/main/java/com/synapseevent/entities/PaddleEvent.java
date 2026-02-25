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
    private String map;

    private Integer capacity;
    private Integer reservation;

    private Double price;
    private Boolean disponibilite = true;

    private String organizer;
    private String description;

    private String status = "draft";

    private final String type = "Paddle";

    public PaddleEvent() {}

    public PaddleEvent(String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                       String location, String map, Integer capacity, Integer reservation,
                       Double price, Boolean disponibilite, String organizer,
                       String description, String status) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.map = map;
        this.capacity = capacity;
        this.reservation = reservation;
        this.price = price;
        setDisponibilite(disponibilite);
        this.organizer = organizer;
        this.description = description;
        setStatus(status);
    }

    public PaddleEvent(Long id, String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                       String location, String map, Integer capacity, Integer reservation,
                       Double price, Boolean disponibilite, String organizer,
                       String description, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.map = map;
        this.capacity = capacity;
        this.reservation = reservation;
        this.price = price;
        setDisponibilite(disponibilite);
        this.organizer = organizer;
        this.description = description;
        setStatus(status);
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

    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getReservation() { return reservation; }
    public void setReservation(Integer reservation) { this.reservation = reservation; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Boolean getDisponibilite() { return disponibilite; }
    public void setDisponibilite(Boolean disponibilite) {
        if (disponibilite == null) this.disponibilite = true;
        else this.disponibilite = disponibilite;
    }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) this.status = "draft";
        else this.status = status;
    }

    public String getType() { return type; }

    @Override
    public String toString() { return name; }
}