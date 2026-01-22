package com.synapseevent.entities;

public class Participant {
    private Long id;
    private User utilisateur;
    private Reservation reservation;
    private String statut;

    public Participant() {}

    public Participant(User utilisateur, Reservation reservation, String statut) {
        this.utilisateur = utilisateur;
        this.reservation = reservation;
        this.statut = statut;
    }

    public Participant(Long id, User utilisateur, Reservation reservation, String statut) {
        this.id = id;
        this.utilisateur = utilisateur;
        this.reservation = reservation;
        this.statut = statut;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUtilisateur() { return utilisateur; }
    public void setUtilisateur(User utilisateur) { this.utilisateur = utilisateur; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() { return utilisateur.getNom() + " " + utilisateur.getPrenom() + " - " + statut; }
}
