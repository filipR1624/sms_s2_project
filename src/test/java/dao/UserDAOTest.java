package dao;

import model.User;
import org.junit.jupiter.api.*;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UserDAO.
 * Uses JUnit 5 (Jupiter) for testing the UserDAO methods.
 */
public class UserDAOTest {

    private UserDAO userDAO;
    private static int testUserId;

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Set up test database or configurations if needed
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        userDAO = new UserDAO();

        // Create a test user
        cleanupTestData(); // Clean any existing test data first
        createTestUser();
    }

    @AfterEach
    public void cleanup() throws SQLException {
        // This method runs after each test
        cleanupTestData();
    }

    @AfterAll
    public static void teardownDatabase() {
        // This method runs once after all tests
        // Clean up database connections or test data
    }

    // Helper method to create a test user
    private void createTestUser() throws SQLException {
        User testUser = new User(
                "Test User",
                "test@example.com",
                "password123",
                User.AccountType.TEACHER,
                "123 Test St",
                "555-1234"
        );

        testUserId = userDAO.addUser(testUser);
        assertNotEquals(0, testUserId, "Failed to create test user");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM User WHERE email = ?")) {
            ps.setString(1, "test@example.com");
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetUserById() {
        // Test retrieving a user by ID
        Optional<User> userOptional = userDAO.getUserById(testUserId);

        assertTrue(userOptional.isPresent(), "User should exist");
        User user = userOptional.get();
        assertEquals("Test User", user.getFullName(), "User name should match");
        assertEquals("test@example.com", user.getEmail(), "User email should match");
        assertEquals(User.AccountType.TEACHER, user.getAccountType(), "User account type should match");
    }

    @Test
    public void testGetUserByEmail() {
        // Test retrieving a user by email
        Optional<User> userOptional = userDAO.getUserByEmail("test@example.com");

        assertTrue(userOptional.isPresent(), "User should exist");
        User user = userOptional.get();
        assertEquals(testUserId, user.getUserId(), "User ID should match");
        assertEquals("Test User", user.getFullName(), "User name should match");
    }

    @Test
    public void testGetAllUsers() {
        // Test retrieving all users
        List<User> users = userDAO.getAllUsers();

        assertFalse(users.isEmpty(), "Users list should not be empty");
        assertTrue(users.stream().anyMatch(user -> user.getEmail().equals("test@example.com")),
                "Users list should contain test user");
    }

    @Test
    public void testGetUsersByType() {
        // Test retrieving users by account type
        List<User> teachers = userDAO.getUsersByType(User.AccountType.TEACHER);

        assertFalse(teachers.isEmpty(), "Teachers list should not be empty");
        assertTrue(teachers.stream().anyMatch(user -> user.getEmail().equals("test@example.com")),
                "Teachers list should contain test user");
    }

    @Test
    public void testUpdateUser() throws SQLException {
        // Test updating a user
        Optional<User> userOptional = userDAO.getUserById(testUserId);
        assertTrue(userOptional.isPresent(), "User should exist for update");

        User user = userOptional.get();
        user.setFullName("Updated Test User");
        user.setAddress("456 Update St");

        boolean updated = userDAO.updateUser(user);
        assertTrue(updated, "User update should succeed");

        // Verify the update
        Optional<User> updatedUserOptional = userDAO.getUserById(testUserId);
        assertTrue(updatedUserOptional.isPresent(), "User should still exist after update");

        User updatedUser = updatedUserOptional.get();
        assertEquals("Updated Test User", updatedUser.getFullName(), "User name should be updated");
        assertEquals("456 Update St", updatedUser.getAddress(), "User address should be updated");
    }

    @Test
    public void testDeleteUser() throws SQLException {
        // Test deleting a user
        boolean deleted = userDAO.deleteUser(testUserId);
        assertTrue(deleted, "User deletion should succeed");

        // Verify the deletion
        Optional<User> deletedUserOptional = userDAO.getUserById(testUserId);
        assertFalse(deletedUserOptional.isPresent(), "User should not exist after deletion");
    }

    @Test
    public void testIsEmailTaken() {
        // Test checking if an email is taken
        boolean emailTaken = userDAO.isEmailTaken("test@example.com", 0);
        assertTrue(emailTaken, "Email should be taken by test user");

        boolean emailNotTaken = userDAO.isEmailTaken("nonexistent@example.com", 0);
        assertFalse(emailNotTaken, "Email should not be taken");

        // Test excluding the current user
        boolean emailNotTakenByOthers = userDAO.isEmailTaken("test@example.com", testUserId);
        assertFalse(emailNotTakenByOthers, "Email should not be considered taken when excluding the current user");
    }

    @Test
    public void testCountUsers() {
        // Test counting users
        int count = userDAO.countUsers();
        assertTrue(count > 0, "User count should be greater than zero");
    }

    @Test
    public void testCountUsersByType() {
        // Test counting users by type
        int teacherCount = userDAO.countUsersByType(User.AccountType.TEACHER);
        assertTrue(teacherCount > 0, "Teacher count should be greater than zero");
    }

    @Test
    public void testUserAuthentication() {
        // Test user authentication
        Optional<User> authenticatedUser = userDAO.authenticateUser("test@example.com", "password123");
        assertTrue(authenticatedUser.isPresent(), "User should be authenticated with correct password");

        Optional<User> failedAuth = userDAO.authenticateUser("test@example.com", "wrongpassword");
        assertFalse(failedAuth.isPresent(), "User should not be authenticated with incorrect password");
    }
}