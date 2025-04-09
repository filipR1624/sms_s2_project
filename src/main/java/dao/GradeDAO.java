package dao;

import model.Grade;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Grade entities.
 * Provides CRUD operations for the Grade table in the database.
 */
public class GradeDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO Grade (mark, subject, student_id, grade_date, comment, teacher_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Grade WHERE grade_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Grade";
    private static final String SELECT_BY_STUDENT_SQL = "SELECT * FROM Grade WHERE student_id = ?";
    private static final String SELECT_BY_TEACHER_SQL = "SELECT * FROM Grade WHERE teacher_id = ?";
    private static final String SELECT_BY_SUBJECT_SQL = "SELECT * FROM Grade WHERE subject = ?";
    private static final String UPDATE_SQL = "UPDATE Grade SET mark = ?, subject = ?, student_id = ?, grade_date = ?, comment = ?, teacher_id = ? WHERE grade_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Grade WHERE grade_id = ?";

    /**
     * Creates a new grade in the database.
     *
     * @param grade The grade object to be added to the database
     * @return The generated grade ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addGrade(Grade grade) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setGradeParameters(ps, grade);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating grade failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating grade failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a grade by its ID.
     *
     * @param gradeId The ID of the grade to retrieve
     * @return An Optional containing the grade if found, or empty if not found
     */
    public Optional<Grade> getGradeById(int gradeId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, gradeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGrade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grade with ID " + gradeId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all grades from the database.
     *
     * @return A list of all grades
     */
    public List<Grade> getAllGrades() {
        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all grades: " + e.getMessage());
        }

        return grades;
    }

    /**
     * Retrieves grades for a specific student.
     *
     * @param studentId The ID of the student
     * @return A list of grades for the specified student
     */
    public List<Grade> getGradesByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_STUDENT_SQL)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grades for student ID " + studentId + ": " + e.getMessage());
        }

        return grades;
    }

    /**
     * Retrieves grades given by a specific teacher.
     *
     * @param teacherId The ID of the teacher
     * @return A list of grades given by the specified teacher
     */
    public List<Grade> getGradesByTeacher(int teacherId) {
        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_TEACHER_SQL)) {

            ps.setInt(1, teacherId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grades for teacher ID " + teacherId + ": " + e.getMessage());
        }

        return grades;
    }

    /**
     * Retrieves grades for a specific subject.
     *
     * @param subject The subject
     * @return A list of grades for the specified subject
     */
    public List<Grade> getGradesBySubject(String subject) {
        List<Grade> grades = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_SUBJECT_SQL)) {

            ps.setString(1, subject);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving grades for subject " + subject + ": " + e.getMessage());
        }

        return grades;
    }

    /**
     * Updates an existing grade record.
     *
     * @param grade The grade object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateGrade(Grade grade) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, String.valueOf(grade.getMark()));
            ps.setString(2, grade.getSubject());
            ps.setInt(3, grade.getStudentId());
            ps.setDate(4, new java.sql.Date(grade.getGradeDate().getTime()));
            ps.setString(5, grade.getComment());
            ps.setInt(6, grade.getTeacherId());
            ps.setInt(7, grade.getGradeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating grade with ID " + grade.getGradeId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a grade from the database.
     *
     * @param gradeId The ID of the grade to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteGrade(int gradeId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, gradeId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting grade with ID " + gradeId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates a grade mark.
     *
     * @param mark The mark to validate
     * @return true if the mark is valid (A-F), false otherwise
     */
    public boolean isValidMark(char mark) {
        return mark >= 'A' && mark <= 'F';
    }

    /**
     * Gets the average grade for a student.
     *
     * @param studentId The ID of the student
     * @return The average grade value (1-5 where A=5, F=1) or 0 if no grades exist
     */
    public double getAverageGradeForStudent(int studentId) {
        List<Grade> grades = getGradesByStudent(studentId);

        // Return 0 if there are no grades
        if (grades.isEmpty()) {
            return 0;
        }

        double total = 0;
        int validGrades = 0;

        for (Grade grade : grades) {
            char mark = grade.getMark();

            // Validate the grade is in range A-F
            if (mark >= 'A' && mark <= 'F' && mark != 'E') {
                // Convert letter grade to numeric value (A=5, B=4, C=3, D=2, F=1)
                // Using character arithmetic: 'F' (70) - 'A' (65) = 5, so A = 5 + 1 - 5 = 5
                // Similarly, F = 5 + 1 - 0 = 1
                double numericValue = 5 - (mark - 'A');

                total += numericValue;
                validGrades++;
            }
        }

        // Avoid division by zero if no valid grades
        if (validGrades == 0) {
            return 0;
        }

        return total / validGrades;
    }

    /**
     * Helper method to map ResultSet to Grade object.
     *
     * @param rs The ResultSet containing grade data
     * @return A Grade object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        return new Grade(
                rs.getInt("grade_id"),
                rs.getString("mark").charAt(0),
                rs.getString("subject"),
                rs.getInt("student_id"),
                new Date(rs.getDate("grade_date").getTime()),
                rs.getString("comment"),
                rs.getInt("teacher_id")
        );
    }

    /**
     * Helper method to set parameters in a PreparedStatement.
     *
     * @param ps The PreparedStatement to set parameters for
     * @param grade The Grade object containing the parameter values
     * @throws SQLException If a database access error occurs
     */
    private void setGradeParameters(PreparedStatement ps, Grade grade) throws SQLException {
        ps.setString(1, String.valueOf(grade.getMark()));
        ps.setString(2, grade.getSubject());
        ps.setInt(3, grade.getStudentId());
        ps.setDate(4, new java.sql.Date(grade.getGradeDate().getTime()));
        ps.setString(5, grade.getComment());
        ps.setInt(6, grade.getTeacherId());
    }
}