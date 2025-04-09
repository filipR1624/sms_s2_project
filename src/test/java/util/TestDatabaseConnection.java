package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test version of DatabaseConnection that uses H2 in-memory database.
 * This allows tests to run without affecting the production database.
 */
public class TestDatabaseConnection {
    // H2 in-memory database URL
    private static final String URL = "jdbc:mysql://localhost:3306/SMS_project";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    // ThreadLocal to hold transaction-specific connections
    private static final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    // Flag to indicate if the database has been initialized
    private static boolean initialized = false;

    // Private constructor to prevent instantiation
    private TestDatabaseConnection() {}

    /**
     * Initializes the test database with the necessary schema
     */
    public static synchronized void initTestDatabase() throws SQLException {
        if (initialized) {
            return;
        }

        try (Connection conn = getConnection()) {
            // Create tables that match your production schema
            try (Statement stmt = conn.createStatement()) {
                // User table
                stmt.execute("CREATE TABLE IF NOT EXISTS User ("
                        + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "fullName VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL UNIQUE, "
                        + "password VARCHAR(255) NOT NULL, "
                        + "accountType VARCHAR(20) NOT NULL, "
                        + "address VARCHAR(255), "
                        + "phone_number VARCHAR(20)"
                        + ")");

                // Class_group table
                stmt.execute("CREATE TABLE IF NOT EXISTS class_group ("
                        + "class_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "size INT NOT NULL, "
                        + "year INT NOT NULL, "
                        + "room_number INT NOT NULL, "
                        + "teacher_id INT"
                        + ")");

                // Parent table
                stmt.execute("CREATE TABLE IF NOT EXISTS Parent ("
                        + "parent_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "no_children INT NOT NULL, "
                        + "FOREIGN KEY (user_id) REFERENCES User(user_id)"
                        + ")");

                // Teacher table
                stmt.execute("CREATE TABLE IF NOT EXISTS Teacher ("
                        + "teacher_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "user_id INT NOT NULL, "
                        + "class_id INT, "
                        + "FOREIGN KEY (user_id) REFERENCES User(user_id), "
                        + "FOREIGN KEY (class_id) REFERENCES class_group(class_id)"
                        + ")");

                // Student table
                stmt.execute("CREATE TABLE IF NOT EXISTS Student ("
                        + "student_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "class_id INT NOT NULL, "
                        + "f_name VARCHAR(50) NOT NULL, "
                        + "l_name VARCHAR(50) NOT NULL, "
                        + "address VARCHAR(255), "
                        + "parent_id INT NOT NULL, "
                        + "FOREIGN KEY (class_id) REFERENCES class_group(class_id), "
                        + "FOREIGN KEY (parent_id) REFERENCES Parent(parent_id)"
                        + ")");

                // Grade table
                stmt.execute("CREATE TABLE IF NOT EXISTS Grade ("
                        + "grade_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "mark CHAR(1) NOT NULL, "
                        + "subject VARCHAR(50) NOT NULL, "
                        + "student_id INT NOT NULL, "
                        + "grade_date DATE NOT NULL, "
                        + "comment TEXT, "
                        + "teacher_id INT NOT NULL, "
                        + "FOREIGN KEY (student_id) REFERENCES Student(student_id), "
                        + "FOREIGN KEY (teacher_id) REFERENCES Teacher(teacher_id)"
                        + ")");

                // Absence table
                stmt.execute("CREATE TABLE IF NOT EXISTS absence ("
                        + "absence_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "student_id INT NOT NULL, "
                        + "absence_date DATE NOT NULL, "
                        + "description TEXT, "
                        + "status BOOLEAN NOT NULL, "
                        + "FOREIGN KEY (student_id) REFERENCES Student(student_id)"
                        + ")");

                // Homework table
                stmt.execute("CREATE TABLE IF NOT EXISTS homework ("
                        + "homework_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "assignment_date DATE NOT NULL, "
                        + "due_date DATE NOT NULL, "
                        + "class_id INT NOT NULL, "
                        + "description TEXT NOT NULL, "
                        + "status BOOLEAN NOT NULL, "
                        + "FOREIGN KEY (class_id) REFERENCES class_group(class_id)"
                        + ")");

                // Insert some initial test data
                stmt.execute("INSERT INTO User (fullName, email, password, accountType, address, phone_number) "
                        + "VALUES ('Test Admin', 'admin@test.com', 'password', 'ADMIN', '123 Admin St', '555-1111')");

                stmt.execute("INSERT INTO User (fullName, email, password, accountType, address, phone_number) "
                        + "VALUES ('Test Teacher', 'teacher@test.com', 'password', 'TEACHER', '123 Teacher St', '555-2222')");

                stmt.execute("INSERT INTO User (fullName, email, password, accountType, address, phone_number) "
                        + "VALUES ('Test Parent', 'parent@test.com', 'password', 'PARENT', '123 Parent St', '555-3333')");

                stmt.execute("INSERT INTO class_group (size, year, room_number, teacher_id) "
                        + "VALUES (25, 2023, 101, 1)");

                stmt.execute("INSERT INTO Parent (user_id, no_children) "
                        + "VALUES (3, 1)");

                stmt.execute("INSERT INTO Teacher (user_id, class_id) "
                        + "VALUES (2, 1)");

                stmt.execute("INSERT INTO Student (class_id, f_name, l_name, address, parent_id) "
                        + "VALUES (1, 'Test', 'Student', '123 Student St', 1)");
            }
        }

        initialized = true;
    }

    /**
     * Gets a database connection. Returns existing transaction connection if present.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = transactionConnection.get();
        if (conn != null) {
            return conn; // Return existing transaction connection
        }
        // Create new non-transactional connection
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(true); // Auto-commit enabled by default
        return conn;
    }

    /**
     * Starts a new transaction with manual commit control
     */
    public static void beginTransaction() throws SQLException {
        if (transactionConnection.get() != null) {
            throw new SQLException("Transaction already in progress");
        }
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(false); // Disable auto-commit
        transactionConnection.set(conn);
    }

    /**
     * Commits the current transaction
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = transactionConnection.get();
        if (conn == null) {
            throw new SQLException("No active transaction to commit");
        }
        try {
            conn.commit();
        } finally {
            closeTransactionConnection();
        }
    }

    /**
     * Rolls back the current transaction
     */
    public static void rollbackTransaction() {
        Connection conn = transactionConnection.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeTransactionConnection();
            }
        }
    }

    /**
     * Closes transaction connection and removes from ThreadLocal
     */
    private static void closeTransactionConnection() {
        Connection conn = transactionConnection.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            transactionConnection.remove();
        }
    }

    /**
     * Checks if a transaction is in progress
     */
    public static boolean isInTransaction() {
        return transactionConnection.get() != null;
    }

    /**
     * Reset the test database by dropping all tables and reinitializing
     */
    public static void resetTestDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop all tables
            stmt.execute("DROP ALL OBJECTS");

            // Reset initialization flag
            initialized = false;

            // Reinitialize the database
            initTestDatabase();
        }
    }
}