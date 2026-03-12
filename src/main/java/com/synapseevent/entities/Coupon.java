package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Coupon {
    private Long id;
    private String code;
    private String type; // PERCENT, FIXED
    private Double value;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Double minSpend;
    private Boolean isActive;
    private String applicableEventTypes; // Comma-separated list
    private LocalDateTime createdAt;
    private String description;

    public Coupon() {
        this.isActive = true;
        this.usedCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Coupon(String code, String type, Double value, LocalDate startDate, LocalDate endDate, 
                Integer usageLimit, Double minSpend) {
        this();
        this.code = code;
        this.type = type;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.minSpend = minSpend;
    }

    // Validation methods
    public boolean isValid() {
        if (!isActive) return false;
        if (startDate != null && LocalDate.now().isBefore(startDate)) return false;
        if (endDate != null && LocalDate.now().isAfter(endDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }

    public boolean isApplicableToEventType(String eventType) {
        if (applicableEventTypes == null || applicableEventTypes.isEmpty()) {
            return true; // Applies to all event types
        }
        String[] types = applicableEventTypes.split(",");
        for (String type : types) {
            if (type.trim().equalsIgnoreCase(eventType)) {
                return true;
            }
        }
        return false;
    }

    public boolean canBeUsed(Double totalAmount) {
        if (!isValid()) return false;
        if (minSpend != null && totalAmount < minSpend) return false;
        return true;
    }

    public void markAsUsed() {
        if (usedCount == null) usedCount = 0;
        usedCount++;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }

    public Double getMinSpend() { return minSpend; }
    public void setMinSpend(Double minSpend) { this.minSpend = minSpend; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getApplicableEventTypes() { return applicableEventTypes; }
    public void setApplicableEventTypes(String applicableEventTypes) { this.applicableEventTypes = applicableEventTypes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { 
        return code + " (" + type + " - " + value + ")"; 
    }
}
