package dao;

import model.Homework;
import org.junit.jupiter.api.*;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HomeworkDAO.
 * Uses JUnit 5 (Jupiter) for testing the HomeworkDAO methods.
 */
public class HomeworkDAOTest {

    private HomeworkDAO homeworkDAO;
    private static int testHomeworkId;
    private static int testClassId = 1; // Assume this class exists

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Ensure the test class exists
        ensureTestPrerequisites();
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        homeworkDAO = new HomeworkDAO();

        // Create a test homework
        cleanupTestData(); // Clean any existing test data first
        createTestHomework();
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

    // Helper method to ensure test prerequisites
    private static void ensureTestPrerequisites() {
        // In a real test, we would ensure that a test class exists
        // Here we'll just assume it exists with ID 1
    }

    // Helper method to create a test homework
    private void createTestHomework() throws SQLException {
        // Create homework assigned today, due in a week
        Calendar assignmentCal = Calendar.getInstance();
        Date assignmentDate = assignmentCal.getTime();

        Calendar dueCal = Calendar.getInstance();
        dueCal.add(Calendar.DAY_OF_MONTH, 7);
        Date dueDate = dueCal.getTime();

        Homework testHomework = new Homework(
                assignmentDate,
                dueDate,
                testClassId,
                "Math problems chapter 5",
                false // not completed initially
        );

        testHomeworkId = homeworkDAO.addHomework(testHomework);
        assertNotEquals(0, testHomeworkId, "Failed to create test homework");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM homework WHERE description = ? AND class_id = ?")) {
            ps.setString(1, "Math problems chapter 5");
            ps.setInt(2, testClassId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetHomeworkById() {
        // Test retrieving a homework by ID
        Optional<Homework> homeworkOptional = homeworkDAO.getHomeworkById(testHomeworkId);

        assertTrue(homeworkOptional.isPresent(), "Homework should exist");
        Homework homework = homeworkOptional.get();
        assertEquals(testClassId, homework.getClassId(), "Class ID should match");
        assertEquals("Math problems chapter 5", homework.getDescription(), "Description should match");
        assertFalse(homework.isStatus(), "Homework should not be completed initially");
    }

    @Test
    public void testGetAllHomework() {
        // Test retrieving all homework assignments
        List<Homework> homeworkList = homeworkDAO.getAllHomework();

        assertFalse(homeworkList.isEmpty(), "Homework list should not be empty");
        assertTrue(homeworkList.stream().anyMatch(h -> h.getHomeworkId() == testHomeworkId),
                "Homework list should contain test homework");
    }

    @Test
    public void testGetHomeworkByClass() {
        // Test retrieving homework by class
        List<Homework> classHomework = homeworkDAO.getHomeworkByClass(testClassId);

        assertFalse(classHomework.isEmpty(), "Class homework list should not be empty");
        assertTrue(classHomework.stream().anyMatch(h -> h.getHomeworkId() == testHomeworkId),
                "Class homework list should contain test homework");
    }

    @Test
    public void testGetHomeworkByStatus() {
        // Test retrieving homework by status
        List<Homework> incompleteHomework = homeworkDAO.getHomeworkByStatus(false);

        assertFalse(incompleteHomework.isEmpty(), "Incomplete homework list should not be empty");
        assertTrue(incompleteHomework.stream().anyMatch(h -> h.getHomeworkId() == testHomeworkId),
                "Incomplete homework list should contain test homework");

        // Mark the homework as completed and test again
        homeworkDAO.updateHomeworkStatus(testHomeworkId, true);

        List<Homework> completeHomework = homeworkDAO.getHomeworkByStatus(true);
        assertTrue(completeHomework.stream().anyMatch(h -> h.getHomeworkId() == testHomeworkId),
                "Complete homework list should now contain test homework");
    }

    @Test
    public void testGetOverdueHomework() {
        // First, create an overdue homework
        try {
            Calendar pastAssignmentCal = Calendar.getInstance();
            pastAssignmentCal.add(Calendar.DAY_OF_MONTH, -14);
            Date pastAssignmentDate = pastAssignmentCal.getTime();

            Calendar pastDueCal = Calendar.getInstance();
            pastDueCal.add(Calendar.DAY_OF_MONTH, -7);
            Date pastDueDate = pastDueCal.getTime();

            Homework overdueHomework = new Homework(
                    pastAssignmentDate,
                    pastDueDate,
                    testClassId,
                    "Overdue assignment",
                    false // not completed
            );

            int overdueHomeworkId = homeworkDAO.addHomework(overdueHomework);
            assertNotEquals(0, overdueHomeworkId, "Failed to create overdue homework");

            // Test retrieving overdue homework
            List<Homework> overdueList = homeworkDAO.getOverdueHomework();

            assertFalse(overdueList.isEmpty(), "Overdue homework list should not be empty");
            assertTrue(overdueList.stream().anyMatch(h -> h.getHomeworkId() == overdueHomeworkId),
                    "Overdue homework list should contain the overdue homework");

            // The recent homework should not be in the overdue list
            assertFalse(overdueList.stream().anyMatch(h -> h.getHomeworkId() == testHomeworkId),
                    "Overdue homework list should not contain non-overdue homework");

            // Clean up the overdue homework
            homeworkDAO.deleteHomework(overdueHomeworkId);

        } catch (SQLException e) {
            fail("Exception while testing overdue homework: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateHomework() {
        // Test updating a homework
        Optional<Homework> homeworkOptional = homeworkDAO.getHomeworkById(testHomeworkId);
        assertTrue(homeworkOptional.isPresent(), "Homework should exist for update");

        Homework homework = homeworkOptional.get();
        homework.setDescription("Updated math assignment");
        homework.setStatus(true); // Mark as completed

        boolean updated = homeworkDAO.updateHomework(homework);
        assertTrue(updated, "Homework update should succeed");

        // Verify the update
        Optional<Homework> updatedHomeworkOptional = homeworkDAO.getHomeworkById(testHomeworkId);
        assertTrue(updatedHomeworkOptional.isPresent(), "Homework should still exist after update");

        Homework updatedHomework = updatedHomeworkOptional.get();
        assertEquals("Updated math assignment", updatedHomework.getDescription(), "Description should be updated");
        assertTrue(updatedHomework.isStatus(), "Homework should be marked as completed");
    }

    @Test
    public void testUpdateHomeworkStatus() {
        // Test updating just the homework status
        boolean updated = homeworkDAO.updateHomeworkStatus(testHomeworkId, true);
        assertTrue(updated, "Homework status update should succeed");

        // Verify the update
        Optional<Homework> updatedHomeworkOptional = homeworkDAO.getHomeworkById(testHomeworkId);
        assertTrue(updatedHomeworkOptional.isPresent(), "Homework should still exist after update");

        Homework updatedHomework = updatedHomeworkOptional.get();
        assertTrue(updatedHomework.isStatus(), "Homework should be marked as completed");
    }

    @Test
    public void testDeleteHomework() {
        // Test deleting a homework
        boolean deleted = homeworkDAO.deleteHomework(testHomeworkId);
        assertTrue(deleted, "Homework deletion should succeed");

        // Verify the deletion
        Optional<Homework> deletedHomeworkOptional = homeworkDAO.getHomeworkById(testHomeworkId);
        assertFalse(deletedHomeworkOptional.isPresent(), "Homework should not exist after deletion");
    }
}