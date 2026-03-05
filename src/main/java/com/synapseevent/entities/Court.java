package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Court {

    private Long id;
    private String name;
    private Long venueId;
    private String venueName;
    private String venueAddress;
    private String venueCity;
    private Boolean isIndoor;
    private Double pricePerHour;
    private Boolean available;
    private String description;
    private String amenities;

    public Court() {}

    public Court(String name, Long venueId, String venueName, Boolean isIndoor, 
                 Double pricePerHour, Boolean available, String description, String amenities) {
        this.name = name;
        this.venueId = venueId;
        this.venueName = venueName;
        this.isIndoor = isIndoor;
        this.pricePerHour = pricePerHour;
        this.available = available;
        this.description = description;
        this.amenities = amenities;
    }

    public Court(Long id, String name, Long venueId, String venueName, String venueAddress,
                 String venueCity, Boolean isIndoor, Double pricePerHour, Boolean available,
                 String description, String amenities) {
        this.id = id;
        this.name = name;
        this.venueId = venueId;
        this.venueName = venueName;
        this.venueAddress = venueAddress;
        this.venueCity = venueCity;
        this.isIndoor = isIndoor;
        this.pricePerHour = pricePerHour;
        this.available = available;
        this.description = description;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getVenueAddress() { return venueAddress; }
    public void setVenueAddress(String venueAddress) { this.venueAddress = venueAddress; }

    public String getVenueCity() { return venueCity; }
    public void setVenueCity(String venueCity) { this.venueCity = venueCity; }
    
    // Alias methods for compatibility
    public String getAddress() { return venueAddress; }
    public void setAddress(String venueAddress) { this.venueAddress = venueAddress; }
    public String getCity() { return venueCity; }
    public void setCity(String venueCity) { this.venueCity = venueCity; }
    public Boolean isIndoor() { return isIndoor; }
    public void setIndoor(Boolean isIndoor) { this.isIndoor = isIndoor; }

    public Boolean getIsIndoor() { return isIndoor; }
    public void setIsIndoor(Boolean isIndoor) { this.isIndoor = isIndoor; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getCourtType() {
        return isIndoor != null && isIndoor ? "Indoor" : "Outdoor";
    }

    public String getFullName() {
        String type = getCourtType();
        return name + " (" + type + ")";
    }

    @Override
    public String toString() { return getFullName(); }
}
