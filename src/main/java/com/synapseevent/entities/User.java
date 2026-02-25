package com.synapseevent.entities;

public class User {

    private Long id;

    private String email;
    private String password;

    private String nom;
    private String prenom;

    private String phone;
    private String address;

    private String profilePicture;

    private Long roleId;
    private Long enterpriseId;

    private Role role;
    private Entreprise enterprise;

    public User() {}

    public User(String email, String nom, String prenom, Long roleId, Long enterpriseId) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.roleId = roleId;
        this.enterpriseId = enterpriseId;
    }

    public User(String email, String password, String nom, String prenom, String phone, String address,
                String profilePicture, Long roleId, Long enterpriseId) {
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.phone = phone;
        this.address = address;
        this.profilePicture = profilePicture;
        this.roleId = roleId;
        this.enterpriseId = enterpriseId;
    }

    public User(Long id, String email, String nom, String prenom, Long roleId, Long enterpriseId) {
        this.id = id;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.roleId = roleId;
        this.enterpriseId = enterpriseId;
    }

    public User(Long id, String email, String password, String nom, String prenom, String phone, String address,
                String profilePicture, Long roleId, Long enterpriseId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.phone = phone;
        this.address = address;
        this.profilePicture = profilePicture;
        this.roleId = roleId;
        this.enterpriseId = enterpriseId;
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

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }

    public Role getRole() { return role; }
    public void setRole(Role role) {
        this.role = role;
        if (role != null) this.roleId = role.getId();
    }

    public Entreprise getEnterprise() { return enterprise; }
    public void setEnterprise(Entreprise enterprise) {
        this.enterprise = enterprise;
        if (enterprise != null) this.enterpriseId = enterprise.getId();
    }

    @Override
    public String toString() { return nom + " " + prenom; }
}