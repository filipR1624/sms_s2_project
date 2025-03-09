package dao;

import model.Homework;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Homework entities.
 * Provides CRUD operations for the Homework table in the database.
 */
public class HomeworkDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO homework (assignment_date, due_date, class_id, description, status) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM homework WHERE homework_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM homework";
    private static final String SELECT_BY_CLASS_SQL = "SELECT * FROM homework WHERE class_id = ?";
    private static final String SELECT_BY_STATUS_SQL = "SELECT * FROM homework WHERE status = ?";
    private static final String SELECT_OVERDUE_SQL = "SELECT * FROM homework WHERE due_date < ? AND status = false";
    private static final String UPDATE_SQL = "UPDATE homework SET assignment_date = ?, due_date = ?, class_id = ?, description = ?, status = ? WHERE homework_id = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE homework SET status = ? WHERE homework_id = ?";
    private static final String DELETE_SQL = "DELETE FROM homework WHERE homework_id = ?";

    /**
     * Creates a new homework assignment in the database.
     *
     * @param homework The homework object to be added to the database
     * @return The generated homework ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addHomework(Homework homework) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, new java.sql.Date(homework.getAssignmentDate().getTime()));
            ps.setDate(2, new java.sql.Date(homework.getDueDate().getTime()));
            ps.setInt(3, homework.getClassId());
            ps.setString(4, homework.getDescription());
            ps.setBoolean(5, homework.isStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating homework failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating homework failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a homework assignment by its ID.
     *
     * @param homeworkId The ID of the homework to retrieve
     * @return An Optional containing the homework if found, or empty if not found
     */
    public Optional<Homework> getHomeworkById(int homeworkId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, homeworkId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHomework(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving homework with ID " + homeworkId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all homework assignments from the database.
     *
     * @return A list of all homework assignments
     */
    public List<Homework> getAllHomework() {
        List<Homework> homeworks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                homeworks.add(mapResultSetToHomework(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all homework assignments: " + e.getMessage());
        }

        return homeworks;
    }

    /**
     * Retrieves homework assignments for a specific class.
     *
     * @param classId The ID of the class
     * @return A list of homework assignments for the specified class
     */
    public List<Homework> getHomeworkByClass(int classId) {
        List<Homework> homeworks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_CLASS_SQL)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homeworks.add(mapResultSetToHomework(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving homework for class ID " + classId + ": " + e.getMessage());
        }

        return homeworks;
    }

    /**
     * Retrieves homework assignments by status.
     *
     * @param status The status to filter by (true = completed, false = not completed)
     * @return A list of homework assignments with the specified status
     */
    public List<Homework> getHomeworkByStatus(boolean status) {
        List<Homework> homeworks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_STATUS_SQL)) {

            ps.setBoolean(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homeworks.add(mapResultSetToHomework(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving homework by status: " + e.getMessage());
        }

        return homeworks;
    }

    /**
     * Retrieves overdue homework assignments.
     *
     * @return A list of overdue homework assignments
     */
    public List<Homework> getOverdueHomework() {
        List<Homework> homeworks = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_OVERDUE_SQL)) {

            ps.setDate(1, new java.sql.Date(new Date().getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    homeworks.add(mapResultSetToHomework(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving overdue homework: " + e.getMessage());
        }

        return homeworks;
    }

    /**
     * Updates an existing homework assignment record.
     *
     * @param homework The homework object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateHomework(Homework homework) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setDate(1, new java.sql.Date(homework.getAssignmentDate().getTime()));
            ps.setDate(2, new java.sql.Date(homework.getDueDate().getTime()));
            ps.setInt(3, homework.getClassId());
            ps.setString(4, homework.getDescription());
            ps.setBoolean(5, homework.isStatus());
            ps.setInt(6, homework.getHomeworkId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating homework with ID " + homework.getHomeworkId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the status of a homework assignment.
     *
     * @param homeworkId The ID of the homework
     * @param status The new status (true = completed, false = not completed)
     * @return true if the update was successful, false otherwise
     */
    public boolean updateHomeworkStatus(int homeworkId, boolean status) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS_SQL)) {

            ps.setBoolean(1, status);
            ps.setInt(2, homeworkId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating homework status for ID " + homeworkId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a homework assignment from the database.
     *
     * @param homeworkId The ID of the homework to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteHomework(int homeworkId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, homeworkId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting homework with ID " + homeworkId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to map ResultSet to Homework object.
     *
     * @param rs The ResultSet containing homework data
     * @return A Homework object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Homework mapResultSetToHomework(ResultSet rs) throws SQLException {
        return new Homework(
                rs.getInt("homework_id"),
                new Date(rs.getDate("assignment_date").getTime()),
                new Date(rs.getDate("due_date").getTime()),
                rs.getInt("class_id"),
                rs.getString("description"),
                rs.getBoolean("status")
        );
    }
}