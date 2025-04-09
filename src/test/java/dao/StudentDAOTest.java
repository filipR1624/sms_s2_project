package dao;

import model.Student;
import org.junit.jupiter.api.*;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StudentDAO.
 * Uses JUnit 5 (Jupiter) for testing the StudentDAO methods.
 */
public class StudentDAOTest {

    private StudentDAO studentDAO;
    private static int testStudentId;
    private static int testClassId = 1; // Assume this class exists
    private static int testParentId = 1; // Assume this parent exists

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Ensure the test class and parent exist
        ensureTestPrerequisites();
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        studentDAO = new StudentDAO();

        // Create a test student
        cleanupTestData(); // Clean any existing test data first
        createTestStudent();
    }

    @AfterEach
    public void cleanup() throws SQLException {
        // This method runs after each test
        cleanupTestData();
    }

    @AfterAll
    public static void teardownDatabase() {
        // This method runs once after all tests
        // Clean up database connections or test data if needed
    }

    // Helper method to ensure test prerequisites
    private static void ensureTestPrerequisites() {
        // In a real test, we would ensure that a test class and parent exist
        // Here we'll just assume they exist with IDs 1
    }

    // Helper method to create a test student
    private void createTestStudent() throws SQLException {
        Student testStudent = new Student(
                testClassId,
                "Test",
                "Student",
                "123 School St",
                testParentId
        );

        testStudentId = studentDAO.addStudent(testStudent);
        assertNotEquals(0, testStudentId, "Failed to create test student");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Student WHERE f_name = ? AND l_name = ?")) {
            ps.setString(1, "Test");
            ps.setString(2, "Student");
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetStudentById() {
        // Test retrieving a student by ID
        Optional<Student> studentOptional = studentDAO.getStudentById(testStudentId);

        assertTrue(studentOptional.isPresent(), "Student should exist");
        Student student = studentOptional.get();
        assertEquals("Test", student.getFirstName(), "Student first name should match");
        assertEquals("Student", student.getLastName(), "Student last name should match");
        assertEquals(testClassId, student.getClassId(), "Class ID should match");
        assertEquals(testParentId, student.getParentId(), "Parent ID should match");
    }

    @Test
    public void testGetAllStudents() {
        // Test retrieving all students
        List<Student> students = studentDAO.getAllStudents();

        assertFalse(students.isEmpty(), "Students list should not be empty");
        assertTrue(students.stream().anyMatch(s -> s.getStudentId() == testStudentId),
                "Students list should contain test student");
    }

    @Test
    public void testGetStudentsByClass() {
        // Test retrieving students by class
        List<Student> classStudents = studentDAO.getStudentsByClass(testClassId);

        assertFalse(classStudents.isEmpty(), "Class students list should not be empty");
        assertTrue(classStudents.stream().anyMatch(s -> s.getStudentId() == testStudentId),
                "Class students list should contain test student");
    }

    @Test
    public void testGetStudentsByParent() {
        // Test retrieving students by parent
        List<Student> parentStudents = studentDAO.getStudentsByParent(testParentId);

        assertFalse(parentStudents.isEmpty(), "Parent students list should not be empty");
        assertTrue(parentStudents.stream().anyMatch(s -> s.getStudentId() == testStudentId),
                "Parent students list should contain test student");
    }

    @Test
    public void testUpdateStudent() {
        // Test updating a student
        Optional<Student> studentOptional = studentDAO.getStudentById(testStudentId);
        assertTrue(studentOptional.isPresent(), "Student should exist for update");

        Student student = studentOptional.get();
        student.setFirstName("Updated");
        student.setLastName("StudentTest");
        student.setAddress("456 Update St");

        boolean updated = studentDAO.updateStudent(student);
        assertTrue(updated, "Student update should succeed");

        // Verify the update
        Optional<Student> updatedStudentOptional = studentDAO.getStudentById(testStudentId);
        assertTrue(updatedStudentOptional.isPresent(), "Student should still exist after update");

        Student updatedStudent = updatedStudentOptional.get();
        assertEquals("Updated", updatedStudent.getFirstName(), "Student first name should be updated");
        assertEquals("StudentTest", updatedStudent.getLastName(), "Student last name should be updated");
        assertEquals("456 Update St", updatedStudent.getAddress(), "Student address should be updated");
    }

    @Test
    public void testDeleteStudent() {
        // Test deleting a student
        boolean deleted = studentDAO.deleteStudent(testStudentId);
        assertTrue(deleted, "Student deletion should succeed");

        // Verify the deletion
        Optional<Student> deletedStudentOptional = studentDAO.getStudentById(testStudentId);
        assertFalse(deletedStudentOptional.isPresent(), "Student should not exist after deletion");
    }

    @Test
    public void testCountStudents() {
        // Test counting students
        int count = studentDAO.countStudents();
        assertTrue(count > 0, "Student count should be greater than zero");
    }

    @Test
    public void testAddStudentWithValidation() throws SQLException {
        // Delete test student first to avoid conflicts
        studentDAO.deleteStudent(testStudentId);

        Student newStudent = new Student(
                testClassId,
                "Validation",
                "Test",
                "789 Validation St",
                testParentId
        );

        try {
            int newStudentId = studentDAO.addStudentWithValidation(newStudent);
            assertTrue(newStudentId > 0, "Adding student with validation should succeed");

            // Clean up
            studentDAO.deleteStudent(newStudentId);
        } catch (IllegalArgumentException e) {
            fail("Student validation should not fail with valid data: " + e.getMessage());
        }
    }

    @Test
    public void testAddStudentWithInvalidValidation() {
        // Test adding student with invalid parent ID
        Student invalidStudent = new Student(
                testClassId,
                "Invalid",
                "Student",
                "999 Invalid St",
                -999 // Invalid parent ID
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            studentDAO.addStudentWithValidation(invalidStudent);
        });

        assertTrue(exception.getMessage().contains("Parent ID"),
                "Exception should mention invalid parent ID");
    }
}