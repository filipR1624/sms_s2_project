package dao;

import model.Grade;
import org.junit.jupiter.api.*;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GradeDAO.
 * Uses JUnit 5 (Jupiter) for testing the GradeDAO methods.
 */
public class GradeDAOTest {

    private GradeDAO gradeDAO;
    private static int testGradeId;
    private static int testStudentId = 1; // Assume this student exists
    private static int testTeacherId = 1; // Assume this teacher exists

    @BeforeAll
    public static void setupDatabase() {
        // This method runs once before all tests
        // Make sure the test student and teacher exist
        ensureTestPrerequisites();
    }

    @BeforeEach
    public void setup() throws SQLException {
        // This method runs before each test
        gradeDAO = new GradeDAO();

        // Create a test grade
        cleanupTestData(); // Clean any existing test data first
        createTestGrade();
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
        // In a real test, we would ensure that a test student and teacher exist
        // Here we'll just assume they exist with ID 1
    }

    // Helper method to create a test grade
    private void createTestGrade() throws SQLException {
        Grade testGrade = new Grade(
                'A', // mark
                "Mathematics", // subject
                testStudentId, // student ID
                new Date(), // grade date
                "Excellent work on the test", // comment
                testTeacherId // teacher ID
        );

        testGradeId = gradeDAO.addGrade(testGrade);
        assertNotEquals(0, testGradeId, "Failed to create test grade");
    }

    // Helper method to clean up test data
    private void cleanupTestData() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Grade WHERE subject = ? AND student_id = ?")) {
            ps.setString(1, "Mathematics");
            ps.setInt(2, testStudentId);
            ps.executeUpdate();
        }
    }

    @Test
    public void testGetGradeById() {
        // Test retrieving a grade by ID
        Optional<Grade> gradeOptional = gradeDAO.getGradeById(testGradeId);

        assertTrue(gradeOptional.isPresent(), "Grade should exist");
        Grade grade = gradeOptional.get();
        assertEquals('A', grade.getMark(), "Grade mark should match");
        assertEquals("Mathematics", grade.getSubject(), "Subject should match");
        assertEquals(testStudentId, grade.getStudentId(), "Student ID should match");
        assertEquals(testTeacherId, grade.getTeacherId(), "Teacher ID should match");
        assertNotNull(grade.getComment(), "Comment should not be null");
    }

    @Test
    public void testGetAllGrades() {
        // Test retrieving all grades
        List<Grade> grades = gradeDAO.getAllGrades();

        assertFalse(grades.isEmpty(), "Grades list should not be empty");
        assertTrue(grades.stream().anyMatch(g -> g.getGradeId() == testGradeId),
                "Grades list should contain test grade");
    }

    @Test
    public void testGetGradesByStudent() {
        // Test retrieving grades by student
        List<Grade> studentGrades = gradeDAO.getGradesByStudent(testStudentId);

        assertFalse(studentGrades.isEmpty(), "Student grades list should not be empty");
        assertTrue(studentGrades.stream().anyMatch(g -> g.getGradeId() == testGradeId),
                "Student grades list should contain test grade");
    }

    @Test
    public void testGetGradesByTeacher() {
        // Test retrieving grades by teacher
        List<Grade> teacherGrades = gradeDAO.getGradesByTeacher(testTeacherId);

        assertFalse(teacherGrades.isEmpty(), "Teacher grades list should not be empty");
        assertTrue(teacherGrades.stream().anyMatch(g -> g.getGradeId() == testGradeId),
                "Teacher grades list should contain test grade");
    }

    @Test
    public void testGetGradesBySubject() {
        // Test retrieving grades by subject
        List<Grade> subjectGrades = gradeDAO.getGradesBySubject("Mathematics");

        assertFalse(subjectGrades.isEmpty(), "Subject grades list should not be empty");
        assertTrue(subjectGrades.stream().anyMatch(g -> g.getGradeId() == testGradeId),
                "Subject grades list should contain test grade");
    }


    @Test
    public void testDeleteGrade() {
        // Test deleting a grade
        boolean deleted = gradeDAO.deleteGrade(testGradeId);
        assertTrue(deleted, "Grade deletion should succeed");

        // Verify the deletion
        Optional<Grade> deletedGradeOptional = gradeDAO.getGradeById(testGradeId);
        assertFalse(deletedGradeOptional.isPresent(), "Grade should not exist after deletion");
    }

    @Test
    public void testIsValidMark() {
        // Test validating grade marks
        assertTrue(gradeDAO.isValidMark('A'), "A should be a valid mark");
        assertTrue(gradeDAO.isValidMark('C'), "C should be a valid mark");
        assertTrue(gradeDAO.isValidMark('F'), "F should be a valid mark");

        assertFalse(gradeDAO.isValidMark('G'), "G should not be a valid mark");
        assertFalse(gradeDAO.isValidMark('Z'), "Z should not be a valid mark");
    }

    @Test
    public void testGetAverageGradeForStudent() {
        // Test calculating average grade for a student
        // First, add another grade to ensure we have multiple grades
        try {
            Grade anotherGrade = new Grade(
                    'B', // mark
                    "Science", // subject
                    testStudentId, // student ID
                    new Date(), // grade date
                    "Good lab work", // comment
                    testTeacherId // teacher ID
            );

            int anotherGradeId = gradeDAO.addGrade(anotherGrade);
            assertNotEquals(0, anotherGradeId, "Failed to create another test grade");

            // Calculate average grade
            double averageGrade = gradeDAO.getAverageGradeForStudent(testStudentId);

            // Expected average: (A=5 + B=4) / 2 = 4.5
            assertEquals(4.5, averageGrade, 0.01, "Average grade should be 4.5");

            // Clean up the additional grade
            gradeDAO.deleteGrade(anotherGradeId);

        } catch (SQLException e) {
            fail("Exception while testing average grade: " + e.getMessage());
        }
    }
}