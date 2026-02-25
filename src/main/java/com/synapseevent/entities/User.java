package com.synapseevent.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "nom")
    private String nom;
    
    @Column(name = "prenom")
    private String prenom;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "profilePicture")
    private String profilePicture;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "entreprise_id")
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

    public User(String email, String password, String nom, String prenom, String phone, String address, String profilePicture, Role role, Entreprise enterprise) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.phone = phone;
        this.address = address;
        this.profilePicture = profilePicture;
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

    public User(Long id, String email, String password, String nom, String prenom, String phone, String address, String profilePicture, Role role, Entreprise enterprise) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.phone = phone;
        this.address = address;
        this.profilePicture = profilePicture;
        this.role = role;
        this.enterprise = enterprise;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Entreprise getEnterprise() { return enterprise; }
    public void setEnterprise(Entreprise enterprise) { this.enterprise = enterprise; }

    @Override
    public String toString() { return nom + " " + prenom; }
}