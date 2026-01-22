package com.synapseevent;

import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.entities.User;
import com.synapseevent.service.RoleService;
import com.synapseevent.service.EntrepriseService;
import com.synapseevent.service.UserService;
import java.sql.SQLException;

public class TestCRUD {
    public static void main(String[] args) {
        // Test Role CRUD
        RoleService roleService = new RoleService();
        Role role = new Role("TestRole");
        roleService.add(role);
        System.out.println("Added Role: " + role.getName());

        // Test Entreprise CRUD
        EntrepriseService entrepriseService = new EntrepriseService();
        Entreprise entreprise = new Entreprise("TestEntreprise", "123456789");
        entrepriseService.add(entreprise);
        System.out.println("Added Entreprise: " + entreprise.getNom());

        // Test User CRUD
        UserService userService = new UserService();
        User user = new User("test@example.com", "TestNom", "TestPrenom", role, entreprise);
        try {
            boolean added = userService.ajouter(user);
            System.out.println("Added User: " + added + " - " + user.getNom() + " " + user.getPrenom());

            // Find by ID
            User found = userService.findbyId(user.getId());
            System.out.println("Found User: " + (found != null ? found.getNom() + " " + found.getPrenom() : "Not found"));

            // Update user
            user.setNom("UpdatedNom");
            boolean updated = userService.modifier(user);
            System.out.println("Updated User: " + updated + " - " + user.getNom() + " " + user.getPrenom());

            // List all users
            System.out.println("All Users:");
            for (User u : userService.readAll()) {
                System.out.println(u.getNom() + " " + u.getPrenom());
            }

            // Delete user
            boolean deleted = userService.supprimer(user);
            System.out.println("Deleted User: " + deleted);

            // List all users after delete
            System.out.println("All Users after delete:");
            for (User u : userService.readAll()) {
                System.out.println(u.getNom() + " " + u.getPrenom());
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        System.out.println("CRUD test completed successfully!");
    }
}