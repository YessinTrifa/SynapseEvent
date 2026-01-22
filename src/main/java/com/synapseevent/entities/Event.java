package com.synapseevent.entities;

import java.math.BigDecimal;

public class Event {
    private Long id;
    private String nom;
    private String type;
    private String description;
    private BigDecimal prixBase;
    private Integer capaciteMax;

    public Event() {}

    public Event(String nom, String type, String description, BigDecimal prixBase, Integer capaciteMax) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prixBase = prixBase;
        this.capaciteMax = capaciteMax;
    }

    public Event(Long id, String nom, String type, String description, BigDecimal prixBase, Integer capaciteMax) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prixBase = prixBase;
        this.capaciteMax = capaciteMax;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrixBase() { return prixBase; }
    public void setPrixBase(BigDecimal prixBase) { this.prixBase = prixBase; }

    public Integer getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(Integer capaciteMax) { this.capaciteMax = capaciteMax; }

    @Override
    public String toString() { return nom; }
}
