package com.synapseevent.entities;

public class TeamBuildingActivity {
    
    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer durationMinutes;
    private Double pricePerPerson;
    private Integer minParticipants;
    private Integer maxParticipants;
    private Boolean isActive;
    
    public TeamBuildingActivity() {
        this.isActive = true;
        this.minParticipants = 1;
        this.maxParticipants = 100;
    }
    
    public TeamBuildingActivity(String name, String description, String category, 
                               Integer durationMinutes, Double pricePerPerson) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.pricePerPerson = pricePerPerson;
        this.isActive = true;
        this.minParticipants = 1;
        this.maxParticipants = 100;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public Double getPricePerPerson() { return pricePerPerson; }
    public void setPricePerPerson(Double pricePerPerson) { this.pricePerPerson = pricePerPerson; }
    
    public Integer getMinParticipants() { return minParticipants; }
    public void setMinParticipants(Integer minParticipants) { this.minParticipants = minParticipants; }
    
    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    @Override
    public String toString() { return name; }
}
