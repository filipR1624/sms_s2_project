package dao;

import model.Parent;
import model.ParentDetailsDTO;
import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Parent entities.
 * Provides CRUD operations for the Parent table in the database.
 */
public class ParentDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO Parent (user_id, no_children) VALUES (?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Parent WHERE parent_id = ?";
    private static final String SELECT_BY_USER_ID_SQL = "SELECT * FROM Parent WHERE user_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Parent";
    private static final String UPDATE_SQL = "UPDATE Parent SET user_id = ?, no_children = ? WHERE parent_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Parent WHERE parent_id = ?";
    private static final String JOIN_USER_SQL =
            "SELECT p.parent_id, p.user_id, p.no_children, " +
                    "u.fullName, u.email, u.password, u.accountType, u.address, u.phone_number " +
                    "FROM parent p JOIN user u ON p.user_id = u.user_id WHERE p.parent_id = ?";

    /**
     * Creates a new parent in the database.
     *
     * @param parent The parent object to be added to the database
     * @return The generated parent ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addParent(Parent parent) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, parent.getUserId());
            ps.setInt(2, parent.getNumberOfChildren());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating parent failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating parent failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a parent by their ID.
     *
     * @param parentId The ID of the parent to retrieve
     * @return An Optional containing the parent if found, or empty if not found
     */
    public Optional<Parent> getParentById(int parentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving parent with ID " + parentId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves a parent by user ID.
     *
     * @param userId The ID of the user associated with the parent
     * @return An Optional containing the parent if found, or empty if not found
     */
    public Optional<Parent> getParentByUserId(int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_USER_ID_SQL)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParent(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving parent with user ID " + userId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all parents from the database.
     *
     * @return A list of all parents
     */
    public List<Parent> getAllParents() {
        List<Parent> parents = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                parents.add(mapResultSetToParent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all parents: " + e.getMessage());
        }

        return parents;
    }

    /**
     * Updates an existing parent record.
     *
     * @param parent The parent object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateParent(Parent parent) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, parent.getUserId());
            ps.setInt(2, parent.getNumberOfChildren());
            ps.setInt(3, parent.getParentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating parent with ID " + parent.getParentId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a parent from the database.
     *
     * @param parentId The ID of the parent to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteParent(int parentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, parentId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting parent with ID " + parentId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a parent with validation.
     *
     * @param parent The parent to add
     * @param user The user object associated with the parent
     * @return The generated parent ID if successful
     * @throws SQLException If a database access error occurs
     * @throws IllegalArgumentException If validation fails
     */
    public int addParentWithValidation(Parent parent, User user) throws SQLException, IllegalArgumentException {
        // Start transaction
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Verify user type is PARENT
            if (user.getAccountType() != User.AccountType.PARENT) {
                throw new IllegalArgumentException("User must have PARENT account type");
            }

            // Add the user first
            UserDAO userDAO = new UserDAO();
            int userId = userDAO.addUser(user);

            // Set the user ID in the parent object
            parent.setUserId(userId);

            // Add the parent
            int parentId = addParent(parent);

            // Commit the transaction
            connection.commit();

            return parentId;
        } catch (SQLException | IllegalArgumentException e) {
            // Rollback the transaction in case of error
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            // Reset auto-commit and close connection
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves parent details including user information.
     *
     * @param parentId The ID of the parent
     * @return An Optional containing parent details if found, or empty if not found
     */
    public Optional<ParentDetailsDTO> getParentDetails(int parentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(JOIN_USER_SQL)) {

            ps.setInt(1, parentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Parent parent = mapResultSetToParent(rs);
                    User user = mapResultSetToUser(rs);
                    return Optional.of(new ParentDetailsDTO(parent, user));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving parent details with ID " + parentId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Gets all parents with their names from the database
     *
     * @return A list of ParentDetailsDTO objects containing parent info with names
     * @throws SQLException If a database error occurs
     */
    public List<ParentDetailsDTO> getAllParentsWithNames() throws SQLException {
        List<ParentDetailsDTO> parentsList = new ArrayList<>();
        String sql = "SELECT p.parent_id, p.user_id, p.number_of_children, u.full_name " +
                "FROM parent p " +
                "JOIN user u ON p.user_id = u.user_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int parentId = resultSet.getInt("parent_id");
                int userId = resultSet.getInt("user_id");
                int numberOfChildren = resultSet.getInt("number_of_children");
                String fullName = resultSet.getString("full_name");

                Parent parent = new Parent(parentId, userId, numberOfChildren);
                // Create a minimal User object with just the name
                User user = new User(fullName, "", "", null, "", "");
                user.setUserId(userId);

                parentsList.add(new ParentDetailsDTO(parent, user));
            }
        }

        return parentsList;
    }

    /**
     * Helper method to map ResultSet to Parent object.
     *
     * @param rs The ResultSet containing parent data
     * @return A Parent object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Parent mapResultSetToParent(ResultSet rs) throws SQLException {
        return new Parent(
                rs.getInt("parent_id"),
                rs.getInt("user_id"),
                rs.getInt("no_children")
        );
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
}