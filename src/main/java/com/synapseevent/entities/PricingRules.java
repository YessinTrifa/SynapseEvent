package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.DayOfWeek;

public class PricingRules {
    private Long id;
    private String eventType; // TeamBuilding, Anniversary, Formation, etc.
    private String ruleType; // VOLUME, EARLY_BIRD, OFF_PEAK, GROUP_SIZE
    private String conditionType; // MIN_PEOPLE, DAYS_BEFORE_EVENT, DAY_OF_WEEK
    private Double conditionValue;
    private String discountType; // PERCENT, FIXED_AMOUNT
    private Double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String description;

    public PricingRules() {
        this.isActive = true;
    }

    public PricingRules(String eventType, String ruleType, String conditionType, 
                    Double conditionValue, String discountType, Double discountValue) {
        this();
        this.eventType = eventType;
        this.ruleType = ruleType;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    // Validation methods
    public boolean isValid() {
        if (!isActive) return false;
        if (startDate != null && LocalDate.now().isBefore(startDate)) return false;
        if (endDate != null && LocalDate.now().isAfter(endDate)) return false;
        return true;
    }

    public boolean appliesToEvent(String eventType) {
        return this.eventType == null || this.eventType.equalsIgnoreCase(eventType) || "ALL".equalsIgnoreCase(this.eventType);
    }

    public boolean appliesToGroupSize(Integer groupSize) {
        if (!"GROUP_SIZE".equalsIgnoreCase(ruleType) || !isValid()) return false;
        if ("MIN_PEOPLE".equalsIgnoreCase(conditionType)) {
            return groupSize >= conditionValue;
        } else if ("EXACT_PEOPLE".equalsIgnoreCase(conditionType)) {
            return groupSize.equals(conditionValue.intValue());
        } else if ("RANGE_MIN".equalsIgnoreCase(conditionType)) {
            return groupSize >= conditionValue;
        }
        return false;
    }

    public boolean appliesToBookingDate(LocalDate bookingDate, LocalDate eventDate) {
        if (!"EARLY_BIRD".equalsIgnoreCase(ruleType) || !isValid()) return false;
        if ("DAYS_BEFORE_EVENT".equalsIgnoreCase(conditionType)) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(bookingDate, eventDate);
            return daysBetween >= conditionValue;
        }
        return false;
    }

    public boolean appliesToDayOfWeek(LocalDate date) {
        if (!"OFF_PEAK".equalsIgnoreCase(ruleType) || !isValid()) return false;
        if ("DAY_OF_WEEK".equalsIgnoreCase(conditionType)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return dayOfWeek.getValue() == conditionValue.intValue();
        }
        return false;
    }

    public Double calculateDiscount(Double basePrice) {
        if (!isValid()) return 0.0;
        
        if ("PERCENT".equalsIgnoreCase(discountType)) {
            return basePrice * (discountValue / 100.0);
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(discountType)) {
            return Math.min(discountValue, basePrice); // Don't exceed base price
        }
        return 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public String getConditionType() { return conditionType; }
    public void setConditionType(String conditionType) { this.conditionType = conditionType; }

    public Double getConditionValue() { return conditionValue; }
    public void setConditionValue(Double conditionValue) { this.conditionValue = conditionValue; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }

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
        return ruleType + " - " + discountValue + " " + discountType; 
    }
}
