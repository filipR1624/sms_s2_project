package dao;

import model.Student;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Student entities.
 * Provides CRUD operations for the Student table in the database.
 */
public class StudentDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO Student (class_id, f_name, l_name, address, parent_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Student WHERE student_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Student";
    private static final String SELECT_BY_CLASS_SQL = "SELECT * FROM Student WHERE class_id = ?";
    private static final String SELECT_BY_PARENT_SQL = "SELECT * FROM Student WHERE parent_id = ?";
    private static final String UPDATE_SQL = "UPDATE Student SET class_id = ?, f_name = ?, l_name = ?, address = ?, parent_id = ? WHERE student_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Student WHERE student_id = ?";
    private static final String COUNT_STUDENTS_SQL = "SELECT COUNT(*) FROM Student";

    /**
     * Creates a new student in the database.
     *
     * @param student The student object to be added to the database
     * @return The generated student ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addStudent(Student student) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setStudentParameters(ps, student);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating student failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a student by their ID.
     *
     * @param studentId The ID of the student to retrieve
     * @return An Optional containing the student if found, or empty if not found
     */
    public Optional<Student> getStudentById(int studentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student with ID " + studentId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all students from the database.
     *
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all students: " + e.getMessage());
        }

        return students;
    }

    /**
     * Retrieves all students in a specific class.
     *
     * @param classId The ID of the class
     * @return A list of students in the specified class
     */
    public List<Student> getStudentsByClass(int classId) {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_CLASS_SQL)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students for class ID " + classId + ": " + e.getMessage());
        }

        return students;
    }

    /**
     * Retrieves all students for a specific parent.
     *
     * @param parentId The ID of the parent
     * @return A list of students for the specified parent
     */
    public List<Student> getStudentsByParent(int parentId) {
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_PARENT_SQL)) {

            ps.setInt(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students for parent ID " + parentId + ": " + e.getMessage());
        }

        return students;
    }

    /**
     * Updates an existing student record.
     *
     * @param student The student object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, student.getClassId());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getAddress());
            ps.setInt(5, student.getParentId());
            ps.setInt(6, student.getStudentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student with ID " + student.getStudentId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a student from the database.
     *
     * @param studentId The ID of the student to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, studentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student with ID " + studentId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a student with validation for parent and class existence.
     *
     * @param student The student to add
     * @return The generated student ID if successful
     * @throws SQLException If a database access error occurs
     * @throws IllegalArgumentException If validation fails
     */
    public int addStudentWithValidation(Student student) throws SQLException, IllegalArgumentException {
        if (!parentExists(student.getParentId())) {
            throw new IllegalArgumentException("Parent ID " + student.getParentId() + " does not exist");
        }
        if (!classExists(student.getClassId())) {
            throw new IllegalArgumentException("Class ID " + student.getClassId() + " does not exist");
        }
        return addStudent(student);
    }

    /**
     * Counts the total number of students in the database.
     *
     * @return The total count of students
     */
    public int countStudents() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_STUDENTS_SQL)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Checks if a parent exists in the database.
     *
     * @param parentId The ID of the parent to check
     * @return true if the parent exists, false otherwise
     */
    private boolean parentExists(int parentId) {
        String sql = "SELECT parent_id FROM parent WHERE parent_id = ?";
        return checkExistence(sql, parentId);
    }

    /**
     * Checks if a class exists in the database.
     *
     * @param classId The ID of the class to check
     * @return true if the class exists, false otherwise
     */
    private boolean classExists(int classId) {
        String sql = "SELECT class_id FROM class_group WHERE class_id = ?";
        return checkExistence(sql, classId);
    }

    /**
     * Helper method to check existence of a record.
     *
     * @param sql The SQL query to execute
     * @param id The ID parameter for the query
     * @return true if a record exists, false otherwise
     */
    private boolean checkExistence(String sql, int id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to map ResultSet to Student object.
     *
     * @param rs The ResultSet containing student data
     * @return A Student object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("student_id"),
                rs.getInt("class_id"),
                rs.getString("f_name"),
                rs.getString("l_name"),
                rs.getString("address"),
                rs.getInt("parent_id")
        );
    }

    /**
     * Helper method to set parameters in a PreparedStatement.
     *
     * @param ps The PreparedStatement to set parameters for
     * @param student The Student object containing the parameter values
     * @throws SQLException If a database access error occurs
     */
    private void setStudentParameters(PreparedStatement ps, Student student) throws SQLException {
        ps.setInt(1, student.getClassId());
        ps.setString(2, student.getFirstName());
        ps.setString(3, student.getLastName());
        ps.setString(4, student.getAddress());
        ps.setInt(5, student.getParentId());
    }
}