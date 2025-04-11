package dao;

import model.Absence;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Absence entities.
 * Provides CRUD operations for the Absence table in the database.
 */
public class AbsenceDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO absence (student_id, absence_date, description, status) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM absence WHERE absence_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM absence";
    private static final String SELECT_BY_STUDENT_SQL = "SELECT * FROM absence WHERE student_id = ?";
    private static final String SELECT_BY_STATUS_SQL = "SELECT * FROM absence WHERE status = ?";
    private static final String UPDATE_SQL = "UPDATE absence SET student_id = ?, absence_date = ?, description = ?, status = ? WHERE absence_id = ?";
    private static final String UPDATE_STATUS_SQL = "UPDATE absence SET status = ? WHERE absence_id = ?";
    private static final String DELETE_SQL = "DELETE FROM absence WHERE absence_id = ?";
    private static final String COUNT_BY_STUDENT_SQL = "SELECT COUNT(*) FROM absence WHERE student_id = ?";

    /**
     * Creates a new absence in the database.
     *
     * @param absence The absence object to be added to the database
     * @return The generated absence ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addAbsence(Absence absence) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, absence.getStudentId());
            ps.setDate(2, new java.sql.Date(absence.getAbsenceDate().getTime()));
            ps.setString(3, absence.getDescription());
            ps.setBoolean(4, absence.isStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating absence failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating absence failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves an absence by its ID.
     *
     * @param absenceId The ID of the absence to retrieve
     * @return An Optional containing the absence if found, or empty if not found
     */
    public Optional<Absence> getAbsenceById(int absenceId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, absenceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAbsence(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving absence with ID " + absenceId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all absences from the database.
     *
     * @return A list of all absences
     */
    public List<Absence> getAllAbsences() {
        List<Absence> absences = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                absences.add(mapResultSetToAbsence(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all absences: " + e.getMessage());
        }

        return absences;
    }

    /**
     * Retrieves absences for a specific student.
     *
     * @param studentId The ID of the student
     * @return A list of absences for the specified student
     */
    public List<Absence> getAbsencesByStudent(int studentId) {
        List<Absence> absences = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_STUDENT_SQL)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToAbsence(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving absences for student ID " + studentId + ": " + e.getMessage());
        }

        return absences;
    }

    /**
     * Retrieves absences by status.
     *
     * @param status The status to filter by (true = excused, false = unexcused)
     * @return A list of absences with the specified status
     */
    public List<Absence> getAbsencesByStatus(boolean status) {
        List<Absence> absences = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_STATUS_SQL)) {

            ps.setBoolean(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    absences.add(mapResultSetToAbsence(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving absences by status: " + e.getMessage());
        }

        return absences;
    }

    /**
     * Updates an existing absence record.
     *
     * @param absence The absence object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateAbsence(Absence absence) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, absence.getStudentId());
            ps.setDate(2, new java.sql.Date(absence.getAbsenceDate().getTime()));
            ps.setString(3, absence.getDescription());
            ps.setBoolean(4, absence.isStatus());
            ps.setInt(5, absence.getAbsenceId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating absence with ID " + absence.getAbsenceId() + ": " + e.getMessage());
            return false;
        }
    }


    /**
     * Updates the status of an absence.
     *
     * @param absenceId The ID of the absence to update
     * @param status The new status (true = excused, false = unexcused)
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateAbsenceStatus(int absenceId, boolean status) throws SQLException {
        String sql = "UPDATE absence SET status = ? WHERE absence_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBoolean(1, status);
            statement.setInt(2, absenceId);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Deletes an absence from the database.
     *
     * @param absenceId The ID of the absence to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteAbsence(int absenceId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, absenceId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting absence with ID " + absenceId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts the number of absences for a student.
     *
     * @param studentId The ID of the student
     * @return The number of absences for the student
     */
    public int countAbsencesByStudent(int studentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(COUNT_BY_STUDENT_SQL)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting absences for student ID " + studentId + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to map ResultSet to Absence object.
     *
     * @param rs The ResultSet containing absence data
     * @return An Absence object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Absence mapResultSetToAbsence(ResultSet rs) throws SQLException {
        return new Absence(
                rs.getInt("absence_id"),
                rs.getInt("student_id"),
                new Date(rs.getDate("absence_date").getTime()),
                rs.getString("description"),
                rs.getBoolean("status")
        );
    }
}