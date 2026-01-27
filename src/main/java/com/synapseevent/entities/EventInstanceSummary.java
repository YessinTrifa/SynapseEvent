package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventInstanceSummary {
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
    private String categoryName;
    private String subcategoryName;
    private String typeName;
    private String variantName;

    public EventInstanceSummary(Long id, String name, LocalDate date, LocalTime startTime, LocalTime endTime,
                               String location, Integer capacity, Double price, String organizer, String description,
                               String categoryName, String subcategoryName, String typeName, String variantName) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.capacity = capacity;
        this.price = price;
        this.organizer = organizer;
        this.description = description;
        this.categoryName = categoryName;
        this.subcategoryName = subcategoryName;
        this.typeName = typeName;
        this.variantName = variantName;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public Integer getCapacity() { return capacity; }
    public Double getPrice() { return price; }
    public String getOrganizer() { return organizer; }
    public String getDescription() { return description; }
    public String getCategoryName() { return categoryName; }
    public String getSubcategoryName() { return subcategoryName; }
    public String getTypeName() { return typeName; }
    public String getVariantName() { return variantName; }

    public String getDisplayName() {
        return name + " (" + variantName + ")";
    }
}