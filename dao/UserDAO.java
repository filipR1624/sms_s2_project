package dao;

import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 * Provides CRUD operations for the User table in the database.
 */
public class UserDAO {
    // SQL Queries
    private static final String INSERT_USER_SQL = "INSERT INTO User (fullName, email, password, accountType, address, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM User WHERE user_id = ?";
    private static final String SELECT_USER_BY_EMAIL = "SELECT * FROM User WHERE email = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM User";
    private static final String SELECT_USERS_BY_TYPE = "SELECT * FROM User WHERE accountType = ?";
    private static final String UPDATE_USER_SQL = "UPDATE User SET fullName = ?, email = ?, password = ?, accountType = ?, address = ?, phone_number = ? WHERE user_id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM User WHERE user_id = ?";
    private static final String CHECK_EMAIL_EXISTS = "SELECT COUNT(*) FROM User WHERE email = ? AND user_id != ?";
    private static final String COUNT_USERS_SQL = "SELECT COUNT(*) FROM User";

    /**
     * Creates a new user in the database.
     *
     * @param user The user object to be added to the database
     * @return The generated user ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addUser(User user) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

            setUserParameters(ps, user);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve
     * @return An Optional containing the user if found, or empty if not found
     */
    public Optional<User> getUserById(int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_ID)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with ID " + userId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email The email of the user to retrieve
     * @return An Optional containing the user if found, or empty if not found
     */
    public Optional<User> getUserByEmail(String email) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user with email " + email + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Retrieves users by account type.
     *
     * @param accountType The account type to filter by
     * @return A list of users with the specified account type
     */
    public List<User> getUsersByType(User.AccountType accountType) {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_USERS_BY_TYPE)) {

            ps.setString(1, accountType.name().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users by type " + accountType + ": " + e.getMessage());
        }

        return users;
    }

    /**
     * Updates an existing user record.
     *
     * @param user The user object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getAccountType().name().toLowerCase());
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getPhoneNumber());
            ps.setInt(7, user.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user with ID " + user.getUserId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_USER_SQL)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user with ID " + userId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an email already exists for another user.
     *
     * @param email  The email to check
     * @param userId The user ID to exclude from the check (for updates)
     * @return true if the email exists for another user, false otherwise
     */
    public boolean isEmailTaken(String email, int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(CHECK_EMAIL_EXISTS)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if email is taken: " + e.getMessage());
        }
        return false;
    }

    /**
     * Counts the total number of users in the database.
     *
     * @return The total count of users
     */
    public int countUsers() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_USERS_SQL)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting users: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Counts users by account type.
     *
     * @param accountType The account type to count
     * @return The count of users with the specified account type
     */
    public int countUsersByType(User.AccountType accountType) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM User WHERE accountType = ?")) {

            ps.setString(1, accountType.name().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting users by type: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to map ResultSet to User object.
     *
     * @param rs The ResultSet containing user data
     * @return A User object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("fullName"),
                rs.getString("email"),
                rs.getString("password"),
                User.AccountType.valueOf(rs.getString("accountType").toUpperCase()),
                rs.getString("address"),
                rs.getString("phone_number")
        );
    }

    /**
     * Helper method to set parameters in a PreparedStatement.
     *
     * @param ps   The PreparedStatement to set parameters for
     * @param user The User object containing the parameter values
     * @throws SQLException If a database access error occurs
     */
    private void setUserParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getFullName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setString(4, user.getAccountType().name().toLowerCase());
        ps.setString(5, user.getAddress());
        ps.setString(6, user.getPhoneNumber());
    }
}