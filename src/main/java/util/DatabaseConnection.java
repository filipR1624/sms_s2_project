package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/sms2";
    private static final String USER = "root";
    private static final String PASSWORD = "8712GALAfala";

    // ThreadLocal to hold transaction-specific connections
    private static final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

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

    public static boolean isInTransaction() {
        return transactionConnection.get() != null;
    }
}