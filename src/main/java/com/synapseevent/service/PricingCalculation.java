package com.synapseevent.service;

import java.util.ArrayList;
import java.util.List;

public class PricingCalculation {
    private Double venueBaseFee;
    private Double venuePerPersonFee;
    private Double activitiesTotal;
    private Double servicesTotal;
    private Double subtotal;
    private Double taxAmount;
    private Double serviceFeeAmount;
    private Double discountAmount;
    private Double total;
    private List<AppliedDiscount> appliedDiscounts;
    private List<AppliedTax> appliedTaxes;
    private AppliedServiceFee appliedServiceFee;

    public PricingCalculation() {
        this.venueBaseFee = 0.0;
        this.venuePerPersonFee = 0.0;
        this.activitiesTotal = 0.0;
        this.servicesTotal = 0.0;
        this.subtotal = 0.0;
        this.taxAmount = 0.0;
        this.serviceFeeAmount = 0.0;
        this.discountAmount = 0.0;
        this.total = 0.0;
        this.appliedDiscounts = new ArrayList<>();
        this.appliedTaxes = new ArrayList<>();
    }

    // Getters and Setters
    public Double getVenueBaseFee() { return venueBaseFee; }
    public void setVenueBaseFee(Double venueBaseFee) { this.venueBaseFee = venueBaseFee; }

    public Double getVenuePerPersonFee() { return venuePerPersonFee; }
    public void setVenuePerPersonFee(Double venuePerPersonFee) { this.venuePerPersonFee = venuePerPersonFee; }

    public Double getActivitiesTotal() { return activitiesTotal; }
    public void setActivitiesTotal(Double activitiesTotal) { this.activitiesTotal = activitiesTotal; }

    public Double getServicesTotal() { return servicesTotal; }
    public void setServicesTotal(Double servicesTotal) { this.servicesTotal = servicesTotal; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getServiceFeeAmount() { return serviceFeeAmount; }
    public void setServiceFeeAmount(Double serviceFeeAmount) { this.serviceFeeAmount = serviceFeeAmount; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public List<AppliedDiscount> getAppliedDiscounts() { return appliedDiscounts; }
    public void setAppliedDiscounts(List<AppliedDiscount> appliedDiscounts) { this.appliedDiscounts = appliedDiscounts; }

    public List<AppliedTax> getAppliedTaxes() { return appliedTaxes; }
    public void setAppliedTaxes(List<AppliedTax> appliedTaxes) { this.appliedTaxes = appliedTaxes; }

    public AppliedServiceFee getAppliedServiceFee() { return appliedServiceFee; }
    public void setAppliedServiceFee(AppliedServiceFee appliedServiceFee) { this.appliedServiceFee = appliedServiceFee; }

    // ── Inner helper classes ──────────────────────────────────────────────────

    public static class AppliedDiscount {
        private String name;
        private String type;
        private Double amount;
        private String description;

        public AppliedDiscount(String name, String type, Double amount, String description) {
            this.name = name;
            this.type = type;
            this.amount = amount;
            this.description = description;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public Double getAmount() { return amount; }
        public String getDescription() { return description; }
    }

    public static class AppliedTax {
        private String name;
        private Double rate;
        private Double amount;

        public AppliedTax(String name, Double rate, Double amount) {
            this.name = name;
            this.rate = rate;
            this.amount = amount;
        }

        public String getName() { return name; }
        public Double getRate() { return rate; }
        public Double getAmount() { return amount; }
    }

    public static class AppliedServiceFee {
        private String name;
        private Double rate;
        private Double amount;

        public AppliedServiceFee(String name, Double rate, Double amount) {
            this.name = name;
            this.rate = rate;
            this.amount = amount;
        }

        public String getName() { return name; }
        public Double getRate() { return rate; }
        public Double getAmount() { return amount; }
    }
}
