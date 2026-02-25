package com.synapseevent.entities;

public class Venue {

    private Long id;

    private String name;
    private String type;
    private String address;
    private String contactInfo;
    private String priceRange;

    private Double rating;

    private String description;
    private String amenities;

    public Venue() {}

    public Venue(String name, String type, String address, String contactInfo,
                 String priceRange, Double rating, String description, String amenities) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.contactInfo = contactInfo;
        this.priceRange = priceRange;
        setRating(rating);
        this.description = description;
        this.amenities = amenities;
    }

    public Venue(Long id, String name, String type, String address, String contactInfo,
                 String priceRange, Double rating, String description, String amenities) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.contactInfo = contactInfo;
        this.priceRange = priceRange;
        setRating(rating);
        this.description = description;
        this.amenities = amenities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) {
        if (rating == null) this.rating = null;
        else if (rating < 0) this.rating = 0.0;
        else if (rating > 5) this.rating = 5.0;
        else this.rating = rating;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    @Override
    public String toString() { return name + " (" + type + ")"; }
}