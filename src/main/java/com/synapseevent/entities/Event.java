package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private Long id;
    private String name;
    private String type;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String city;
    private String address;
    private String map;
    private Integer capacity;
    private Double price;
    private String description;
    private String status;
    
    public Event() {}
    
    public Event(Long id, String name, String type, LocalDate date, LocalTime startTime, LocalTime endTime,
                 String location, String city, String address, String map, Integer capacity, 
                 Double price, String description, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.city = city;
        this.address = address;
        this.map = map;
        this.capacity = capacity;
        this.price = price;
        this.description = description;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getMap() { return map; }
    public void setMap(String map) { this.map = map; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
