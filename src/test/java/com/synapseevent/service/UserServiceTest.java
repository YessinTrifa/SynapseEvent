package com.synapseevent.service;

import com.synapseevent.entities.User;
import com.synapseevent.entities.Role;
import com.synapseevent.entities.Entreprise;
import com.synapseevent.utils.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

public class UserServiceTest {

    private UserService userService;
    private RoleService roleService;
    private EntrepriseService entrepriseService;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    @BeforeEach
    public void setUp() {
        userService = new UserService();
        roleService = new RoleService();
        entrepriseService = new EntrepriseService();
        
        // Ensure test data exists
        ensureTestDataExists();
    }
    
    private void ensureTestDataExists() {
        // Create Role if not exists
        if (roleService.getAll().isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("Admin");
            roleService.add(adminRole);
            
            Role userRole = new Role();
            userRole.setName("User");
            roleService.add(userRole);
        }
        
        // Create Enterprise if not exists
        if (entrepriseService.getAll().isEmpty()) {
            Entreprise entreprise = new Entreprise();
            entreprise.setNom("TestCorp");
            entreprise.setSiret("12345678901234");
            entrepriseService.add(entreprise);
        }
    }

    @Test
    public void testAjouterUser() throws SQLException {
        // Get existing test data from database
        List<Role> roles = roleService.getAll();
        List<Entreprise> entreprises = entrepriseService.getAll();
        
        assertFalse(roles.isEmpty(), "Roles should exist in database");
        assertFalse(entreprises.isEmpty(), "Entreprises should exist in database");
        
        // Use the first available role and entreprise
        Role role = roles.get(0);
        Entreprise entreprise = entreprises.get(0);

        User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, role.getId(), entreprise.getId());

        boolean result = userService.ajouter(user);
        assertTrue(result, "User should be added successfully");
        assertNotNull(user.getId(), "User ID should be set after insertion");
    }

    @Test
    public void testReadAllUsers() throws SQLException {
        // First add a test user if none exist
        List<User> users = userService.readAll();
        if (users.isEmpty()) {
            List<Role> roles = roleService.getAll();
            List<Entreprise> entreprises = entrepriseService.getAll();
            if (!roles.isEmpty() && !entreprises.isEmpty()) {
                User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, roles.get(0).getId(), entreprises.get(0).getId());
                userService.ajouter(user);
            }
        }
        
        users = userService.readAll();
        assertNotNull(users, "Users list should not be null");
        assertTrue(users.size() > 0, "Users list should contain at least one user");
    }

    @Test
    public void testFindByEmail() throws SQLException {
        // First add a test user if none exist
        List<User> users = userService.readAll();
        if (users.isEmpty()) {
            List<Role> roles = roleService.getAll();
            List<Entreprise> entreprises = entrepriseService.getAll();
            if (!roles.isEmpty() && !entreprises.isEmpty()) {
                User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, roles.get(0).getId(), entreprises.get(0).getId());
                try {
                    userService.ajouter(user);
                } catch (SQLException e) {
                    fail("Failed to create test user");
                }
            }
        }
        
        users = userService.readAll();
        assertFalse(users.isEmpty(), "At least one user should exist for this test");
        
        User user = userService.findByEmail(users.get(0).getEmail());
        assertNotNull(user, "User should be found by email");
        assertEquals(users.get(0).getEmail(), user.getEmail());
    }

    @Test
    public void testAuthenticate() throws SQLException {
        // First add a test user if none exist
        List<User> users = userService.readAll();
        if (users.isEmpty()) {
            List<Role> roles = roleService.getAll();
            List<Entreprise> entreprises = entrepriseService.getAll();
            if (!roles.isEmpty() && !entreprises.isEmpty()) {
                User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, roles.get(0).getId(), entreprises.get(0).getId());
                try {
                    userService.ajouter(user);
                } catch (SQLException e) {
                    fail("Failed to create test user");
                }
            }
        }
        
        users = userService.readAll();
        assertFalse(users.isEmpty(), "At least one user should exist for this test");
        
        User user = userService.authenticate(users.get(0).getEmail(), "password123");
        assertNotNull(user, "Authentication should succeed with correct credentials");
        assertEquals(users.get(0).getEmail(), user.getEmail());
    }

    @Test
    public void testAuthenticateInvalidPassword() throws SQLException {
        // First add a test user if none exist
        List<User> users = userService.readAll();
        if (users.isEmpty()) {
            List<Role> roles = roleService.getAll();
            List<Entreprise> entreprises = entrepriseService.getAll();
            if (!roles.isEmpty() && !entreprises.isEmpty()) {
                User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, roles.get(0).getId(), entreprises.get(0).getId());
                try {
                    userService.ajouter(user);
                } catch (SQLException e) {
                    fail("Failed to create test user");
                }
            }
        }
        
        users = userService.readAll();
        assertFalse(users.isEmpty(), "At least one user should exist for this test");
        
        User user = userService.authenticate(users.get(0).getEmail(), "wrongpassword");
        assertNull(user, "Authentication should fail with incorrect password");
    }
}
