package com.app.rating.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // *** RENDER POSTGRESQL CREDENTIALS ***
    // Replace these placeholders with your actual Render DB connection details
    private static final String URL = "jdbc:postgresql://<YOUR_RENDER_HOST>:<YOUR_RENDER_PORT>/<YOUR_DATABASE_NAME>";
    private static final String USER = "<YOUR_DATABASE_USER>";
    private static final String PASSWORD = "<YOUR_DATABASE_PASSWORD>";
    
    // RENDER NOTE: Render may require setting the 'sslmode=require' property in the URL 
    // for secure connections, depending on your setup. E.g., "...db_name?ssl=true&sslmode=require"

    public static Connection getConnection() throws SQLException {
        // Load the PostgreSQL JDBC driver (modern JDBC often does this automatically, but it's safe practice)
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("PostgreSQL Driver not available.");
        }
        
        System.out.println("Connecting to database...");
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
