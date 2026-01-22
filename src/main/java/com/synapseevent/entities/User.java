package com.synapseevent.entities;

public class User {
    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private Role role;
    private Entreprise enterprise;

    // Constructors
    public User() {}
    public User(String email, String nom, String prenom, Role role, Entreprise enterprise) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.enterprise = enterprise;
    }

    public User(Long id, String email, String nom, String prenom, Role role, Entreprise enterprise) {
        this.id = id;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.enterprise = enterprise;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Entreprise getEnterprise() { return enterprise; }
    public void setEnterprise(Entreprise enterprise) { this.enterprise = enterprise; }

    @Override
    public String toString() { return nom + " " + prenom; }
}