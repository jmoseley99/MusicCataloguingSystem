package test;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.*;

import src.DatabaseConnection;

/**
 * This class is used to test the DatabaseConnection class can be created successfully so it can be used
 * across the application. It also tests that the connection can be closed.
 * @author Jacob Moseley
 *
 */
public class DatabaseConnectionTest {
	
	@Test
	public void testDatabaseConnectionCreation() {
		DatabaseConnection conn = DatabaseConnection.getInstance();
		assertTrue(conn.close());
	}
}
