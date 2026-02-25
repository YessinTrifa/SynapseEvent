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

        RoleService roleService = new RoleService();
        EntrepriseService entrepriseService = new EntrepriseService();
        UserService userService = new UserService();

        try {
            Role role = new Role("TestRole");
            roleService.add(role);
            System.out.println("Added Role: " + role.getName() + " id=" + role.getId());

            Entreprise entreprise = new Entreprise("TestEntreprise", "123456789");
            entrepriseService.add(entreprise);
            System.out.println("Added Entreprise: " + entreprise.getNom() + " id=" + entreprise.getId());

            User user = new User(
                    "test@example.com",
                    "TestPassword123",
                    "TestNom",
                    "TestPrenom",
                    "22112233",
                    "Tunis",
                    null,
                    role.getId(),
                    entreprise.getId()
            );
            user.setRole(role);
            user.setEnterprise(entreprise);

            boolean added = userService.ajouter(user);
            System.out.println("Added User: " + added + " id=" + user.getId() + " - " + user.getNom() + " " + user.getPrenom());

            User found = userService.findbyId(user.getId());
            System.out.println("Found User: " + (found != null ? found.getNom() + " " + found.getPrenom() : "Not found"));

            user.setNom("UpdatedNom");
            boolean updated = userService.modifier(user);
            System.out.println("Updated User: " + updated + " - " + user.getNom() + " " + user.getPrenom());

            System.out.println("All Users:");
            for (User u : userService.readAll()) {
                System.out.println(u.getId() + " | " + u.getNom() + " " + u.getPrenom() + " | " + u.getEmail());
            }

            boolean deleted = userService.supprimer(user);
            System.out.println("Deleted User: " + deleted);

            System.out.println("All Users after delete:");
            for (User u : userService.readAll()) {
                System.out.println(u.getId() + " | " + u.getNom() + " " + u.getPrenom() + " | " + u.getEmail());
            }

            System.out.println("CRUD test completed successfully!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}