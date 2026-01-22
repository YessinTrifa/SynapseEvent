package com.synapseevent.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private Long id;
    private BigDecimal montant;
    private String statut;
    private LocalDate datePaiement;
    private Reservation reservation;

    public Payment() {}

    public Payment(BigDecimal montant, String statut, LocalDate datePaiement, Reservation reservation) {
        this.montant = montant;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.reservation = reservation;
    }

    public Payment(Long id, BigDecimal montant, String statut, LocalDate datePaiement, Reservation reservation) {
        this.id = id;
        this.montant = montant;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.reservation = reservation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDate getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDate datePaiement) { this.datePaiement = datePaiement; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    @Override
    public String toString() { return "Payment of " + montant + " for reservation " + reservation.getId(); }
}
