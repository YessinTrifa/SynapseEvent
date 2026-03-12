package com.synapseevent.service;

import java.time.LocalDate;

public class PaymentScheduleRequest {
    private Long bookingId;
    private Double totalAmount;
    private Double depositPercentage;
    private Integer installmentCount;
    private LocalDate eventDate;
    private String paymentFrequency; // MONTHLY, BI_WEEKLY, WEEKLY
    private Boolean autoReminders;

    public PaymentScheduleRequest() {
        this.depositPercentage = 30.0; // Default 30% deposit
        this.installmentCount = 2; // Default 2 payments
        this.autoReminders = true; // Default enable reminders
    }

    public PaymentScheduleRequest(Long bookingId, Double totalAmount, LocalDate eventDate) {
        this();
        this.bookingId = bookingId;
        this.totalAmount = totalAmount;
        this.eventDate = eventDate;
    }

    // Getters and Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getDepositPercentage() { return depositPercentage; }
    public void setDepositPercentage(Double depositPercentage) { this.depositPercentage = depositPercentage; }

    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public String getPaymentFrequency() { return paymentFrequency; }
    public void setPaymentFrequency(String paymentFrequency) { this.paymentFrequency = paymentFrequency; }

    public Boolean getAutoReminders() { return autoReminders; }
    public void setAutoReminders(Boolean autoReminders) { this.autoReminders = autoReminders; }
}
