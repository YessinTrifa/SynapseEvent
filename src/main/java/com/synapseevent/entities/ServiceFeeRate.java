package com.synapseevent.entities;

import java.time.LocalDate;

public class ServiceFeeRate {
    private Long id;
    private String name;
    private Double rate; // Percentage (e.g., 5.0 for 5%)
    private String type; // PERCENTAGE, FIXED_AMOUNT
    private String applicability; // ALL, VENUE_ONLY, ACTIVITY_ONLY
    private String eventType; // Specific event type, null for all
    private Double minAmount; // Minimum amount to apply fee
    private Double maxAmount; // Maximum fee cap
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String description;

    public ServiceFeeRate() {
        this.isActive = true;
        this.type = "PERCENTAGE";
        this.applicability = "ALL";
    }

    public ServiceFeeRate(String name, Double rate, String type, String applicability) {
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

    public boolean appliesToAmount(Double amount) {
        if (!isValid()) return false;
        if (minAmount != null && amount < minAmount) return false;
        if (maxAmount != null && amount > maxAmount) return false;
        return true;
    }

    public Double calculateServiceFee(Double baseAmount) {
        if (!isValid() || !appliesToAmount(baseAmount)) return 0.0;
        
        Double fee = 0.0;
        
        if ("PERCENTAGE".equalsIgnoreCase(type)) {
            fee = baseAmount * (rate / 100.0);
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(type)) {
            fee = rate;
        }
        
        // Apply maximum cap if set
        if (maxAmount != null && fee > maxAmount) {
            fee = maxAmount;
        }
        
        return fee;
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

    public Double getMinAmount() { return minAmount; }
    public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }

    public Double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }

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
