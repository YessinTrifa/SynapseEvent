package com.synapseevent.entities;

import java.math.BigDecimal;

public class Option {
    private Long id;
    private String nom;
    private BigDecimal prix;
    private String statut;
    private Event evenement;

    public Option() {}

    public Option(String nom, BigDecimal prix, String statut, Event evenement) {
        this.nom = nom;
        this.prix = prix;
        this.statut = statut;
        this.evenement = evenement;
    }

    public Option(Long id, String nom, BigDecimal prix, String statut, Event evenement) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.statut = statut;
        this.evenement = evenement;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Event getEvenement() { return evenement; }
    public void setEvenement(Event evenement) { this.evenement = evenement; }

    @Override
    public String toString() { return nom + " - " + prix; }
}
