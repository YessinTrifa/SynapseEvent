package com.synapseevent.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentSchedule {
    private Long id;
    private Long bookingId;
    private Integer installmentNumber;
    private Double amount;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String status; // PENDING, PAID, OVERDUE, CANCELLED
    private String paymentMethod;
    private String description;
    private Boolean isDeposit;
    private LocalDateTime createdAt;
    private LocalDateTime reminderSent;

    public PaymentSchedule() {
        this.status = "PENDING";
        this.isDeposit = false;
        this.createdAt = LocalDateTime.now();
    }

    public PaymentSchedule(Long bookingId, Integer installmentNumber, Double amount, 
                       LocalDate dueDate, String description) {
        this();
        this.bookingId = bookingId;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.dueDate = dueDate;
        this.description = description;
    }

    // Business logic methods
    public boolean isOverdue() {
        return "PENDING".equalsIgnoreCase(status) && 
               LocalDate.now().isAfter(dueDate);
    }

    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(status);
    }

    public boolean needsReminder() {
        return "PENDING".equalsIgnoreCase(status) && 
               reminderSent == null &&
               LocalDate.now().plusDays(3).isAfter(dueDate);
    }

    public void markAsPaid(String paymentMethod) {
        this.status = "PAID";
        this.paidDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
    }

    public void markAsOverdue() {
        if ("PENDING".equalsIgnoreCase(status) && LocalDate.now().isAfter(dueDate)) {
            this.status = "OVERDUE";
        }
    }

    public void markReminderSent() {
        this.reminderSent = LocalDateTime.now();
    }

    // Static factory methods for common payment schedules
    public static PaymentSchedule createDepositPayment(Long bookingId, Double totalAmount, 
                                               Double depositPercentage, LocalDate eventDate) {
        Double depositAmount = totalAmount * (depositPercentage / 100.0);
        LocalDate depositDueDate = LocalDate.now().plusDays(7); // Due within 7 days
        
        return new PaymentSchedule(bookingId, 1, depositAmount, depositDueDate, 
                              "Deposit payment (" + depositPercentage + "%)");
    }

    public static PaymentSchedule createFinalPayment(Long bookingId, Double totalAmount, 
                                            Double depositAmount, LocalDate eventDate) {
        Double finalAmount = totalAmount - depositAmount;
        LocalDate finalDueDate = eventDate.minusDays(7); // Due 7 days before event
        
        PaymentSchedule payment = new PaymentSchedule(bookingId, 2, finalAmount, finalDueDate, 
                                                "Final payment");
        payment.setIsDeposit(false);
        return payment;
    }

    public static PaymentSchedule createInstallmentPayment(Long bookingId, Integer installmentNumber, 
                                                  Double amount, LocalDate dueDate) {
        PaymentSchedule payment = new PaymentSchedule(bookingId, installmentNumber, amount, dueDate, 
                                                "Installment " + installmentNumber);
        payment.setIsDeposit(false);
        return payment;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Integer getInstallmentNumber() { return installmentNumber; }
    public void setInstallmentNumber(Integer installmentNumber) { this.installmentNumber = installmentNumber; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsDeposit() { return isDeposit; }
    public void setIsDeposit(Boolean isDeposit) { this.isDeposit = isDeposit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReminderSent() { return reminderSent; }
    public void setReminderSent(LocalDateTime reminderSent) { this.reminderSent = reminderSent; }

    @Override
    public String toString() { 
        return "Payment " + installmentNumber + ": " + amount + " (" + status + ")"; 
    }
}
