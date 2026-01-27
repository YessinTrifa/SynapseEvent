package com.synapseevent.entities;

import java.time.LocalTime;

public class EventTemplate {
    private Long id;
    private String name;
    private String eventType;
    private LocalTime defaultStartTime;
    private LocalTime defaultEndTime;
    private Integer defaultCapacity;
    private Double defaultPrice;
    private String defaultCategory;
    private String defaultDescription;
    private String templateDescription;

    // Constructors
    public EventTemplate() {}

    public EventTemplate(String name, String eventType, LocalTime defaultStartTime, LocalTime defaultEndTime,
                        Integer defaultCapacity, Double defaultPrice, String defaultCategory,
                        String defaultDescription, String templateDescription) {
        this.name = name;
        this.eventType = eventType;
        this.defaultStartTime = defaultStartTime;
        this.defaultEndTime = defaultEndTime;
        this.defaultCapacity = defaultCapacity;
        this.defaultPrice = defaultPrice;
        this.defaultCategory = defaultCategory;
        this.defaultDescription = defaultDescription;
        this.templateDescription = templateDescription;
    }

    public EventTemplate(Long id, String name, String eventType, LocalTime defaultStartTime, LocalTime defaultEndTime,
                        Integer defaultCapacity, Double defaultPrice, String defaultCategory,
                        String defaultDescription, String templateDescription) {
        this.id = id;
        this.name = name;
        this.eventType = eventType;
        this.defaultStartTime = defaultStartTime;
        this.defaultEndTime = defaultEndTime;
        this.defaultCapacity = defaultCapacity;
        this.defaultPrice = defaultPrice;
        this.defaultCategory = defaultCategory;
        this.defaultDescription = defaultDescription;
        this.templateDescription = templateDescription;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalTime getDefaultStartTime() { return defaultStartTime; }
    public void setDefaultStartTime(LocalTime defaultStartTime) { this.defaultStartTime = defaultStartTime; }

    public LocalTime getDefaultEndTime() { return defaultEndTime; }
    public void setDefaultEndTime(LocalTime defaultEndTime) { this.defaultEndTime = defaultEndTime; }

    public Integer getDefaultCapacity() { return defaultCapacity; }
    public void setDefaultCapacity(Integer defaultCapacity) { this.defaultCapacity = defaultCapacity; }

    public Double getDefaultPrice() { return defaultPrice; }
    public void setDefaultPrice(Double defaultPrice) { this.defaultPrice = defaultPrice; }

    public String getDefaultCategory() { return defaultCategory; }
    public void setDefaultCategory(String defaultCategory) { this.defaultCategory = defaultCategory; }

    public String getDefaultDescription() { return defaultDescription; }
    public void setDefaultDescription(String defaultDescription) { this.defaultDescription = defaultDescription; }

    public String getTemplateDescription() { return templateDescription; }
    public void setTemplateDescription(String templateDescription) { this.templateDescription = templateDescription; }

    @Override
    public String toString() {
        return "EventTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eventType='" + eventType + '\'' +
                ", defaultStartTime=" + defaultStartTime +
                ", defaultEndTime=" + defaultEndTime +
                ", defaultCapacity=" + defaultCapacity +
                ", defaultPrice=" + defaultPrice +
                ", defaultCategory='" + defaultCategory + '\'' +
                ", defaultDescription='" + defaultDescription + '\'' +
                ", templateDescription='" + templateDescription + '\'' +
                '}';
    }
}