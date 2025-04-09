package dao;

import model.Teacher;
import model.TeacherDetailsDTO;
import model.User;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Teacher entities.
 * Provides CRUD operations for the Teacher table in the database.
 */
public class TeacherDAO {
    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO Teacher (user_id, class_id) VALUES (?, ?)";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM Teacher WHERE teacher_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Teacher";
    private static final String SELECT_BY_CLASS_ID_SQL = "SELECT * FROM Teacher WHERE class_id = ?";
    private static final String UPDATE_SQL = "UPDATE Teacher SET user_id = ?, class_id = ? WHERE teacher_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Teacher WHERE teacher_id = ?";
    private static final String JOIN_USER_SQL =
            "SELECT t.teacher_id, t.user_id, t.class_id, " +
                    "u.fullName, u.email, u.password, u.accountType, u.address, u.phone_number " +
                    "FROM teacher t JOIN user u ON t.user_id = u.user_id WHERE t.teacher_id = ?";
    private static final String JOIN_USER_BY_CLASS_SQL =
            "SELECT t.teacher_id, t.user_id, t.class_id, " +
                    "u.fullName, u.email, u.password, u.accountType, u.address, u.phone_number " +
                    "FROM teacher t JOIN user u ON t.user_id = u.user_id WHERE t.class_id = ?";
    private static final String COUNT_TEACHERS_SQL = "SELECT COUNT(*) FROM Teacher";

    /**
     * Creates a new teacher in the database.
     *
     * @param teacher The teacher object to be added to the database
     * @return The generated teacher ID if successful
     * @throws SQLException If a database access error occurs
     */
    public int addTeacher(Teacher teacher) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, teacher.getUserId());
            ps.setInt(2, teacher.getClassId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating teacher failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Creating teacher failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a teacher by their ID.
     *
     * @param teacherId The ID of the teacher to retrieve
     * @return An Optional containing the teacher if found, or empty if not found
     */
    public Optional<Teacher> getTeacherById(int teacherId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, teacherId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher with ID " + teacherId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves all teachers from the database.
     *
     * @return A list of all teachers
     */
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all teachers: " + e.getMessage());
        }

        return teachers;
    }

    /**
     * Retrieves teachers by class ID.
     *
     * @param classId The ID of the class
     * @return A list of teachers for the specified class
     */
    public List<Teacher> getTeachersByClassId(int classId) {
        List<Teacher> teachers = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_CLASS_ID_SQL)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    teachers.add(mapResultSetToTeacher(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teachers by class ID " + classId + ": " + e.getMessage());
        }

        return teachers;
    }

    /**
     * Retrieves a teacher by user ID.
     *
     * @param userId The ID of the user associated with the teacher
     * @return An Optional containing the teacher if found, or empty if not found
     */
    public Optional<Teacher> getTeacherByUserId(int userId) {
        String sql = "SELECT * FROM Teacher WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Teacher(
                            rs.getInt("teacher_id"),
                            rs.getInt("user_id"),
                            rs.getInt("class_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher with user ID " + userId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Updates an existing teacher record.
     *
     * @param teacher The teacher object with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateTeacher(Teacher teacher) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setInt(1, teacher.getUserId());
            ps.setInt(2, teacher.getClassId());
            ps.setInt(3, teacher.getTeacherId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating teacher with ID " + teacher.getTeacherId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a teacher from the database.
     *
     * @param teacherId The ID of the teacher to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteTeacher(int teacherId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, teacherId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting teacher with ID " + teacherId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a teacher with validation for user type and class existence.
     *
     * @param teacher The teacher to add
     * @return The generated teacher ID if successful
     * @throws SQLException If a database access error occurs
     * @throws IllegalArgumentException If validation fails
     */
    public int addTeacherWithValidation(Teacher teacher) throws SQLException, IllegalArgumentException {
        if (!isValidTeacherUser(teacher.getUserId())) {
            throw new IllegalArgumentException("User ID " + teacher.getUserId() + " is not a valid teacher account");
        }
        if (!classExists(teacher.getClassId())) {
            throw new IllegalArgumentException("Class ID " + teacher.getClassId() + " does not exist");
        }
        return addTeacher(teacher);
    }

    /**
     * Counts the total number of teachers in the database.
     *
     * @return The total count of teachers
     */
    public int countTeachers() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_TEACHERS_SQL)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting teachers: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Checks if a user is a valid teacher.
     *
     * @param userId The ID of the user to check
     * @return true if the user is a valid teacher, false otherwise
     */
    private boolean isValidTeacherUser(int userId) {
        String sql = "SELECT accountType FROM user WHERE user_id = ?";
        try {
            return checkExistence(sql, userId) &&
                    getAccountType(userId) == User.AccountType.TEACHER;
        } catch (SQLException e) {
            System.err.println("Error validating teacher user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the account type for a user.
     *
     * @param userId The ID of the user
     * @return The account type of the user
     * @throws SQLException If a database access error occurs
     */
    private User.AccountType getAccountType(int userId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT accountType FROM user WHERE user_id = ?")) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return User.AccountType.valueOf(rs.getString("accountType").toUpperCase());
                }
                throw new SQLException("User not found");
            }
        }
    }

    /**
     * Checks if a class exists in the database.
     *
     * @param classId The ID of the class to check
     * @return true if the class exists, false otherwise
     */
    public boolean classExists(int classId) {
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
     * Retrieves teacher details including user information.
     *
     * @param teacherId The ID of the teacher
     * @return An Optional containing teacher details if found, or empty if not found
     */
    public Optional<TeacherDetailsDTO> getTeacherDetails(int teacherId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(JOIN_USER_SQL)) {

            ps.setInt(1, teacherId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    User user = mapResultSetToUser(rs);
                    return Optional.of(new TeacherDetailsDTO(teacher, user));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher details with ID " + teacherId + ": " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Retrieves teacher details for a specific class.
     *
     * @param classId The ID of the class
     * @return A list of teacher details for the specified class
     */
    public List<TeacherDetailsDTO> getTeacherDetailsByClass(int classId) {
        List<TeacherDetailsDTO> teacherDetails = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(JOIN_USER_BY_CLASS_SQL)) {

            ps.setInt(1, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    User user = mapResultSetToUser(rs);
                    teacherDetails.add(new TeacherDetailsDTO(teacher, user));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teacher details for class ID " + classId + ": " + e.getMessage());
        }

        return teacherDetails;
    }

    /**
     * Helper method to map ResultSet to Teacher object.
     *
     * @param rs The ResultSet containing teacher data
     * @return A Teacher object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("teacher_id"),
                rs.getInt("user_id"),
                rs.getInt("class_id")
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