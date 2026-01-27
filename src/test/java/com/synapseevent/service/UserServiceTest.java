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
    }

    @Test
    public void testAjouterUser() throws SQLException {
        // Create test data
        Role role = new Role(1L, "User");
        Entreprise entreprise = new Entreprise(1L, "TestCorp", "123456789");

        User user = new User("testuser" + System.currentTimeMillis() + "@example.com", "password123", "Test", "User", "1234567890", "Test Address", null, role, entreprise);

        boolean result = userService.ajouter(user);
        assertTrue(result, "User should be added successfully");
        assertNotNull(user.getId(), "User ID should be set after insertion");
    }

    @Test
    public void testReadAllUsers() throws SQLException {
        List<User> users = userService.readAll();
        assertNotNull(users, "Users list should not be null");
        assertTrue(users.size() >= 0, "Users list should contain at least the sample data");
    }

    @Test
    public void testFindByEmail() {
        User user = userService.findByEmail("admin@techcorp.com");
        assertNotNull(user, "User should be found by email");
        assertEquals("admin@techcorp.com", user.getEmail());
    }

    @Test
    public void testAuthenticate() {
        User user = userService.authenticate("admin@techcorp.com", "password123");
        assertNotNull(user, "Authentication should succeed with correct credentials");
        assertEquals("admin@techcorp.com", user.getEmail());
    }

    @Test
    public void testAuthenticateInvalidPassword() {
        User user = userService.authenticate("admin@techcorp.com", "wrongpassword");
        assertNull(user, "Authentication should fail with incorrect password");
    }
}