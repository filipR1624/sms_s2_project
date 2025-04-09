package dao;

import model.ClassGroup;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ClassGroup entities.
 * Provides CRUD operations for the class_group table in the database.
 */
public class ClassGroupDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO class_group (size, year, room_number, teacher_id) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM class_group WHERE class_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM class_group";
    private static final String SELECT_BY_TEACHER_SQL = "SELECT * FROM class_group WHERE teacher_id = ?";
    private static final String UPDATE_SQL = "UPDATE class_group SET size = ?, year = ?, room_number = ?, teacher_id = ? WHERE class_id = ?";
    private static final String UPDATE_TEACHER_SQL = "UPDATE class_group SET teacher_id = ? WHERE class_id = ?";
    private static final String DELETE_SQL = "DELETE FROM class_group WHERE class_id = ?";
    private static final String COUNT_CLASSES_SQL = "SELECT COUNT(*) FROM class_group";

    /**
     * Creates a new class in the database.
     *
     * @param classGroup The class object to be added to the database
     * @return The generated class ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addClass(ClassGroup classGroup) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, classGroup.getSize());
            ps.setInt(2, classGroup.getYear());
            ps.setInt(3, classGroup.getRoomNumber());
            ps.setInt(4, classGroup.getTeacherId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating class failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating class failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a class by its ID.
     *
     * @param classId The ID of the class to retrieve
     * @return An Optional containing the class if found, or empty if not found
     */
    public Optional<ClassGroup> getClassById(int classId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving class with ID " + classId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all classes from the database.
     *
     * @return A list of all classes
     */
    public List<ClassGroup> getAllClasses() {
        List<ClassGroup> classes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                classes.add(mapResultSetToClass(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all classes: " + e.getMessage());
        }

        return classes;
    }

    /**
     * Retrieves classes assigned to a specific teacher.
     *
     * @param teacherId The ID of the teacher
     * @return A list of classes assigned to the specified teacher
     */
    public List<ClassGroup> getClassesByTeacher(int teacherId) {
        List<ClassGroup> classes = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_TEACHER_SQL)) {

            ps.setInt(1, teacherId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classes.add(mapResultSetToClass(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving classes for teacher ID " + teacherId + ": " + e.getMessage());
        }

        return classes;
    }

    /**
     * Updates an existing class record.
     *
     * @param classGroup The class object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateClass(ClassGroup classGroup) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, classGroup.getSize());
            ps.setInt(2, classGroup.getYear());
            ps.setInt(3, classGroup.getRoomNumber());
            ps.setInt(4, classGroup.getTeacherId());
            ps.setInt(5, classGroup.getClassId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating class with ID " + classGroup.getClassId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the teacher assigned to a class.
     *
     * @param classId The ID of the class
     * @param teacherId The ID of the teacher
     * @return true if the update was successful, false otherwise
     */
    public boolean updateClassTeacher(int classId, int teacherId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_TEACHER_SQL)) {

            ps.setInt(1, teacherId);
            ps.setInt(2, classId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating teacher for class ID " + classId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a class from the database.
     *
     * @param classId The ID of the class to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteClass(int classId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, classId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting class with ID " + classId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts the total number of classes in the database.
     *
     * @return The total count of classes
     */
    public int countClasses() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_CLASSES_SQL)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting classes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to map ResultSet to ClassGroup object.
     *
     * @param rs The ResultSet containing class data
     * @return A ClassGroup object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private ClassGroup mapResultSetToClass(ResultSet rs) throws SQLException {
        int teacherId = rs.getInt("teacher_id");
        // Check if teacher_id is null (0 indicates NULL in MySQL for INT)
        if (rs.wasNull()) {
            teacherId = 0;
        }

        return new ClassGroup(
                rs.getInt("class_id"),
                rs.getInt("size"),
                rs.getInt("year"),
                rs.getInt("room_number"),
                teacherId
        );
    }
}