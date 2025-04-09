package dao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import util.TestDatabaseConnection;

import java.sql.SQLException;

/**
 * Base class for all DAO tests.
 * Provides common setup and teardown methods for the test database.
 */
public abstract class BaseDAOTest {

    @BeforeAll
    public static void setupTestDatabase() throws SQLException {
        // Initialize the test database with schema and initial data
        TestDatabaseConnection.initTestDatabase();
    }

    @BeforeEach
    public void setupTest() throws SQLException {
        // Override in subclasses if needed
    }

    @AfterEach
    public void cleanupTest() throws SQLException {
        // Override in subclasses if needed
    }

    @AfterAll
    public static void teardownTestDatabase() throws SQLException {
        // Reset the test database after all tests
        TestDatabaseConnection.resetTestDatabase();
    }
}