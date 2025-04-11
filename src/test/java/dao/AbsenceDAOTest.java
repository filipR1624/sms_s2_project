package dao;

import model.Absence;
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
 * Test class for AbsenceDAO.
 * Uses JUnit 5 (Jupiter) for testing the AbsenceDAO methods.
 */
public class AbsenceDAOTest {

    private AbsenceDAO absenceDAO;
    private static int testAbsenceId;
    private static int testStudentId = 1; // Assume this student exists

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Make sure the test student exists
        ensureTestPrerequisites();
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        absenceDAO = new AbsenceDAO();

        // Create a test absence
        cleanupTestData(); // Clean any existing test data first
        createTestAbsence();
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
        // In a real test, we would ensure that a test student exists
        // Here, we'll just assume they exist with ID 1
    }

    // Helper method to create a test absence
    private void createTestAbsence() throws SQLException {
        // Create absence for yesterday
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date absenceDate = calendar.getTime();

        Absence testAbsence = new Absence(
                testStudentId,
                absenceDate,
                "Sick leave",
                false // unexcused initially
        );

        testAbsenceId = absenceDAO.addAbsence(testAbsence);
        assertNotEquals(0, testAbsenceId, "Failed to create test absence");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM absence WHERE description = ? AND student_id = ?")) {
            ps.setString(1, "Sick leave");
            ps.setInt(2, testStudentId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetAbsenceById() {
        // Test retrieving an absence by ID
        Optional<Absence> absenceOptional = absenceDAO.getAbsenceById(testAbsenceId);

        assertTrue(absenceOptional.isPresent(), "Absence should exist");
        Absence absence = absenceOptional.get();
        assertEquals(testStudentId, absence.getStudentId(), "Student ID should match");
        assertEquals("Sick leave", absence.getDescription(), "Description should match");
        assertFalse(absence.isStatus(), "Absence should be unexcused initially");
    }

    @Test
    public void testGetAllAbsences() {
        // Test retrieving all absences
        List<Absence> absences = absenceDAO.getAllAbsences();

        assertFalse(absences.isEmpty(), "Absences list should not be empty");
        assertTrue(absences.stream().anyMatch(a -> a.getAbsenceId() == testAbsenceId),
                "Absences list should contain test absence");
    }

    @Test
    public void testGetAbsencesByStudent() {
        // Test retrieving absences by student
        List<Absence> studentAbsences = absenceDAO.getAbsencesByStudent(testStudentId);

        assertFalse(studentAbsences.isEmpty(), "Student absences list should not be empty");
        assertTrue(studentAbsences.stream().anyMatch(a -> a.getAbsenceId() == testAbsenceId),
                "Student absences list should contain test absence");
    }

    @Test
    public void testGetAbsencesByStatus() throws SQLException {
        // Test retrieving absences by status
        List<Absence> unexcusedAbsences = absenceDAO.getAbsencesByStatus(false);

        assertFalse(unexcusedAbsences.isEmpty(), "Unexcused absences list should not be empty");
        assertTrue(unexcusedAbsences.stream().anyMatch(a -> a.getAbsenceId() == testAbsenceId),
                "Unexcused absences list should contain test absence");

        // Mark the absence as excused and test again
        absenceDAO.updateAbsenceStatus(testAbsenceId, true);

        List<Absence> excusedAbsences = absenceDAO.getAbsencesByStatus(true);
        assertTrue(excusedAbsences.stream().anyMatch(a -> a.getAbsenceId() == testAbsenceId),
                "Excused absences list should now contain test absence");
    }

    @Test
    public void testUpdateAbsence() {
        // Test updating an absence
        Optional<Absence> absenceOptional = absenceDAO.getAbsenceById(testAbsenceId);
        assertTrue(absenceOptional.isPresent(), "Absence should exist for update");

        Absence absence = absenceOptional.get();
        absence.setDescription("Doctor's appointment");
        absence.setStatus(true); // Mark as excused

        boolean updated = absenceDAO.updateAbsence(absence);
        assertTrue(updated, "Absence update should succeed");

        // Verify the update
        Optional<Absence> updatedAbsenceOptional = absenceDAO.getAbsenceById(testAbsenceId);
        assertTrue(updatedAbsenceOptional.isPresent(), "Absence should still exist after update");

        Absence updatedAbsence = updatedAbsenceOptional.get();
        assertEquals("Doctor's appointment", updatedAbsence.getDescription(), "Description should be updated");
        assertTrue(updatedAbsence.isStatus(), "Absence should be marked as excused");
    }

    @Test
    public void testUpdateAbsenceStatus() throws SQLException {
        // Test updating just the absence status
        boolean updated = absenceDAO.updateAbsenceStatus(testAbsenceId, true);
        assertTrue(updated, "Absence status update should succeed");

        // Verify the update
        Optional<Absence> updatedAbsenceOptional = absenceDAO.getAbsenceById(testAbsenceId);
        assertTrue(updatedAbsenceOptional.isPresent(), "Absence should still exist after update");

        Absence updatedAbsence = updatedAbsenceOptional.get();
        assertTrue(updatedAbsence.isStatus(), "Absence should be marked as excused");
    }

    @Test
    public void testDeleteAbsence() {
        // Test deleting an absence
        boolean deleted = absenceDAO.deleteAbsence(testAbsenceId);
        assertTrue(deleted, "Absence deletion should succeed");

        // Verify the deletion
        Optional<Absence> deletedAbsenceOptional = absenceDAO.getAbsenceById(testAbsenceId);
        assertFalse(deletedAbsenceOptional.isPresent(), "Absence should not exist after deletion");
    }

    @Test
    public void testCountAbsencesByStudent() {
        // Test counting absences for a student
        int count = absenceDAO.countAbsencesByStudent(testStudentId);
        assertTrue(count > 0, "Absence count should be greater than zero");

        // Add another absence for the same student
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            Date olderAbsenceDate = calendar.getTime();

            Absence anotherAbsence = new Absence(
                    testStudentId,
                    olderAbsenceDate,
                    "Family emergency",
                    true // excused
            );

            int anotherAbsenceId = absenceDAO.addAbsence(anotherAbsence);
            assertNotEquals(0, anotherAbsenceId, "Failed to create another test absence");

            // Count should now be increased
            int newCount = absenceDAO.countAbsencesByStudent(testStudentId);
            assertEquals(count + 1, newCount, "Absence count should be increased by 1");

            // Clean up the additional absence
            absenceDAO.deleteAbsence(anotherAbsenceId);

        } catch (SQLException e) {
            fail("Exception while testing absence count: " + e.getMessage());
        }
    }
}