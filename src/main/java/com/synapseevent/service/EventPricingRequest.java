package com.synapseevent.service;

import com.synapseevent.entities.*;
import java.time.LocalDate;
import java.util.List;

public class EventPricingRequest {
    private String eventType;
    private Venue venue;
    private List<TeamBuildingActivity> activities;
    private Integer headcount;
    private LocalDate bookingDate;
    private LocalDate eventDate;
    private String couponCode;
    private Double additionalServices;
    private String customerType; // INDIVIDUAL, CORPORATE, NON_PROFIT

    public EventPricingRequest() {}

    public EventPricingRequest(String eventType, Venue venue, List<TeamBuildingActivity> activities,
                           Integer headcount, LocalDate bookingDate, LocalDate eventDate) {
        this.eventType = eventType;
        this.venue = venue;
        this.activities = activities;
        this.headcount = headcount;
        this.bookingDate = bookingDate;
        this.eventDate = eventDate;
    }

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Venue getVenue() { return venue; }
    public void setVenue(Venue venue) { this.venue = venue; }

    public List<TeamBuildingActivity> getActivities() { return activities; }
    public void setActivities(List<TeamBuildingActivity> activities) { this.activities = activities; }

    public Integer getHeadcount() { return headcount; }
    public void setHeadcount(Integer headcount) { this.headcount = headcount; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public Double getAdditionalServices() { return additionalServices; }
    public void setAdditionalServices(Double additionalServices) { this.additionalServices = additionalServices; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
}
