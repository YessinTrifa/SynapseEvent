package com.synapseevent.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "entreprises")
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "siret")
    private String siret;

    public Entreprise() {}

    public Entreprise(String nom, String siret) {
        this.nom = nom;
        this.siret = siret;
    }

    public Entreprise(Long id, String nom, String siret) {
        this.id = id;
        this.nom = nom;
        this.siret = siret;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    @Override
    public String toString() { return nom; }
}