package com.synapseevent.entities;

import java.time.LocalDate;

public class Reservation {
    private Long id;
    private LocalDate date;
    private String lieu;
    private Integer nombreParticipants;
    private String statut;
    private User utilisateur;
    private Event evenement;

    public Reservation() {}

    public Reservation(LocalDate date, String lieu, Integer nombreParticipants, String statut, User utilisateur, Event evenement) {
        this.date = date;
        this.lieu = lieu;
        this.nombreParticipants = nombreParticipants;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
    }

    public Reservation(Long id, LocalDate date, String lieu, Integer nombreParticipants, String statut, User utilisateur, Event evenement) {
        this.id = id;
        this.date = date;
        this.lieu = lieu;
        this.nombreParticipants = nombreParticipants;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.evenement = evenement;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }

    public Integer getNombreParticipants() { return nombreParticipants; }
    public void setNombreParticipants(Integer nombreParticipants) { this.nombreParticipants = nombreParticipants; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }

    public Event getEvenement() { return evenement; }
    public void setEvenement(Event evenement) { this.evenement = evenement; }

    @Override
    public String toString() { return "Reservation for " + evenement.getNom() + " on " + date; }
}
