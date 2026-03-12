package com.synapseevent.entities;

import java.time.LocalDate;

public class TaxRate {
    private Long id;
    private String name;
    private Double rate; // Percentage (e.g., 10.0 for 10%)
    private String type; // PERCENTAGE, FIXED_AMOUNT
    private String applicability; // ALL, VENUE, ACTIVITY, SERVICE
    private String eventType; // Specific event type, null for all
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String description;

    public TaxRate() {
        this.isActive = true;
        this.type = "PERCENTAGE";
        this.applicability = "ALL";
    }

    public TaxRate(String name, Double rate, String type, String applicability) {
        this();
        this.name = name;
        this.rate = rate;
        this.type = type;
        this.applicability = applicability;
    }

    // Validation methods
    public boolean isValid() {
        if (!isActive) return false;
        if (startDate != null && LocalDate.now().isBefore(startDate)) return false;
        if (endDate != null && LocalDate.now().isAfter(endDate)) return false;
        return true;
    }

    public boolean appliesToEventType(String eventType) {
        return this.eventType == null || 
               this.eventType.equalsIgnoreCase(eventType) || 
               "ALL".equalsIgnoreCase(this.eventType);
    }

    public Double calculateTax(Double taxableAmount) {
        if (!isValid()) return 0.0;
        
        if ("PERCENTAGE".equalsIgnoreCase(type)) {
            return taxableAmount * (rate / 100.0);
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(type)) {
            return rate;
        }
        return 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getApplicability() { return applicability; }
    public void setApplicability(String applicability) { this.applicability = applicability; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { 
        return name + " (" + rate + "%)"; 
    }
}
