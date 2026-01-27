package com.synapseevent.entities;

public class UserPreferences {
    private Long id;
    private Long userId;
    private String preferredCategories; // comma-separated
    private String preferredLocations;
    private Double maxPrice;
    private Integer minRating;

    // Constructors
    public UserPreferences() {}

    public UserPreferences(Long userId, String preferredCategories, String preferredLocations, Double maxPrice, Integer minRating) {
        this.userId = userId;
        this.preferredCategories = preferredCategories;
        this.preferredLocations = preferredLocations;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    public UserPreferences(Long id, Long userId, String preferredCategories, String preferredLocations, Double maxPrice, Integer minRating) {
        this.id = id;
        this.userId = userId;
        this.preferredCategories = preferredCategories;
        this.preferredLocations = preferredLocations;
        this.maxPrice = maxPrice;
        this.minRating = minRating;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(String preferredCategories) { this.preferredCategories = preferredCategories; }

    public String getPreferredLocations() { return preferredLocations; }
    public void setPreferredLocations(String preferredLocations) { this.preferredLocations = preferredLocations; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Integer getMinRating() { return minRating; }
    public void setMinRating(Integer minRating) { this.minRating = minRating; }

    @Override
    public String toString() {
        return "UserPreferences{" +
                "id=" + id +
                ", userId=" + userId +
                ", preferredCategories='" + preferredCategories + '\'' +
                ", preferredLocations='" + preferredLocations + '\'' +
                ", maxPrice=" + maxPrice +
                ", minRating=" + minRating +
                '}';
    }
}