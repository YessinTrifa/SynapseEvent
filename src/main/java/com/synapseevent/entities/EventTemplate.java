package com.synapseevent.entities;

import java.time.LocalTime;

import jakarta.persistence.*;

@Entity
@Table(name = "event_templates")
public class EventTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "eventType")
    private String eventType;
    
    @Column(name = "defaultStartTime")
    private LocalTime defaultStartTime;
    
    @Column(name = "defaultEndTime")
    private LocalTime defaultEndTime;
    
    @Column(name = "defaultCapacity")
    private Integer defaultCapacity;
    
    @Column(name = "defaultPrice")
    private Double defaultPrice;
    
    @Column(name = "defaultCategory")
    private String defaultCategory;
    
    @Column(name = "defaultDescription", columnDefinition = "TEXT")
    private String defaultDescription;
    
    @Column(name = "templateDescription", columnDefinition = "TEXT")
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