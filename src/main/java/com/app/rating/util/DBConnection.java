package com.app.rating.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles establishing and closing the JDBC connection to the PostgreSQL database.
 */
public class DBConnection {

    // *** IMPORTANT: REPLACE THESE WITH YOUR RENDER POSTGRESQL DETAILS ***
    // Example: jdbc:postgresql://<external_host>:<port>/<database_name>
    private static final String URL = "jdbc:postgresql://YOUR_RENDER_HOST:5432/YOUR_DATABASE_NAME?ssl=true&sslmode=require";
    private static final String USER = "YOUR_DATABASE_USER";
    private static final String PASSWORD = "YOUR_DATABASE_PASSWORD";
    
    // NOTE: The '?ssl=true&sslmode=require' is typically necessary for secure connections on cloud hosts like Render.

    public static Connection getConnection() throws SQLException {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Check pom.xml dependency.");
            throw new SQLException("PostgreSQL Driver not available.", e);
        }
        
        System.out.println("Attempting connection to PostgreSQL...");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
