package dao;

import model.ClassGroup;
import org.junit.jupiter.api.*;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ClassGroupDAO.
 * Uses JUnit 5 (Jupiter) for testing the ClassGroupDAO methods.
 */
public class ClassGroupDAOTest {

    private ClassGroupDAO classDAO;
    private static int testClassId;
    private static int testTeacherId = 1; // Assume this teacher exists

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Make sure the test teacher exists
        ensureTestPrerequisites();
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        classDAO = new ClassGroupDAO();

        // Create a test class
        cleanupTestData(); // Clean any existing test data first
        createTestClass();
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
        // In a real test, we would ensure that a test teacher exists
        // Here we'll just assume they exist with ID 1
    }

    // Helper method to create a test class
    private void createTestClass() throws SQLException {
        ClassGroup testClass = new ClassGroup(
                25, // size
                2023, // year
                101, // room number
                testTeacherId // teacher ID
        );

        testClassId = classDAO.addClass(testClass);
        assertNotEquals(0, testClassId, "Failed to create test class");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM class_group WHERE room_number = ? AND year = ?")) {
            ps.setInt(1, 101);
            ps.setInt(2, 2023);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetClassById() {
        // Test retrieving a class by ID
        Optional<ClassGroup> classOptional = classDAO.getClassById(testClassId);

        assertTrue(classOptional.isPresent(), "Class should exist");
        ClassGroup classGroup = classOptional.get();
        assertEquals(25, classGroup.getSize(), "Class size should match");
        assertEquals(2023, classGroup.getYear(), "Class year should match");
        assertEquals(101, classGroup.getRoomNumber(), "Room number should match");
        assertEquals(testTeacherId, classGroup.getTeacherId(), "Teacher ID should match");
    }

    @Test
    public void testGetAllClasses() {
        // Test retrieving all classes
        List<ClassGroup> classes = classDAO.getAllClasses();

        assertFalse(classes.isEmpty(), "Classes list should not be empty");
        assertTrue(classes.stream().anyMatch(c -> c.getClassId() == testClassId),
                "Classes list should contain test class");
    }

    @Test
    public void testGetClassesByTeacher() {
        // Test retrieving classes by teacher
        List<ClassGroup> teacherClasses = classDAO.getClassesByTeacher(testTeacherId);

        assertFalse(teacherClasses.isEmpty(), "Teacher classes list should not be empty");
        assertTrue(teacherClasses.stream().anyMatch(c -> c.getClassId() == testClassId),
                "Teacher classes list should contain test class");
    }

    @Test
    public void testUpdateClass() {
        // Test updating a class
        Optional<ClassGroup> classOptional = classDAO.getClassById(testClassId);
        assertTrue(classOptional.isPresent(), "Class should exist for update");

        ClassGroup classGroup = classOptional.get();
        classGroup.setSize(30);
        classGroup.setRoomNumber(202);

        boolean updated = classDAO.updateClass(classGroup);
        assertTrue(updated, "Class update should succeed");

        // Verify the update
        Optional<ClassGroup> updatedClassOptional = classDAO.getClassById(testClassId);
        assertTrue(updatedClassOptional.isPresent(), "Class should still exist after update");

        ClassGroup updatedClass = updatedClassOptional.get();
        assertEquals(30, updatedClass.getSize(), "Class size should be updated");
        assertEquals(202, updatedClass.getRoomNumber(), "Room number should be updated");
    }

    @Test
    public void testUpdateClassTeacher() {
        // Test updating a class teacher
        int newTeacherId = 2; // Assume this teacher exists

        boolean updated = classDAO.updateClassTeacher(testClassId, newTeacherId);
        assertTrue(updated, "Class teacher update should succeed");

        // Verify the update
        Optional<ClassGroup> updatedClassOptional = classDAO.getClassById(testClassId);
        assertTrue(updatedClassOptional.isPresent(), "Class should still exist after update");

        ClassGroup updatedClass = updatedClassOptional.get();
        assertEquals(newTeacherId, updatedClass.getTeacherId(), "Teacher ID should be updated");
    }

    @Test
    public void testDeleteClass() {
        // Test deleting a class
        boolean deleted = classDAO.deleteClass(testClassId);
        assertTrue(deleted, "Class deletion should succeed");

        // Verify the deletion
        Optional<ClassGroup> deletedClassOptional = classDAO.getClassById(testClassId);
        assertFalse(deletedClassOptional.isPresent(), "Class should not exist after deletion");
    }

    @Test
    public void testCountClasses() {
        // Test counting classes
        int count = classDAO.countClasses();
        assertTrue(count > 0, "Class count should be greater than zero");
    }
}