package com.app.rating.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // *** RENDER POSTGRESQL CREDENTIALS EXTRACTED FROM SCREENSHOT ***
    private static final String DB_HOST = "dpg-d3stp6vgi27c73dudc40-a";
    private static final String DB_PORT = "5432";
    private static final String DB_NAME = "javaproj_db";
    private static final String DB_USER = "javaproj_db_user";
    private static final String DB_PASSWORD = "vhXs8OwUh4tDxaOn6ciZljsImnZpyl80";
    
    // Construct the JDBC URL with SSL enabled, as required by Render's external URL.
    private static final String URL = 
        "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + 
        "?ssl=true&sslmode=require";
    
    public static Connection getConnection() throws SQLException {
        // Load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Check pom.xml dependency.");
            e.printStackTrace();
            throw new SQLException("PostgreSQL Driver not available.", e);
        }
        
        System.out.println("Attempting connection to Render PostgreSQL...");
        // DriverManager automatically attempts to close any resources it opens on connection failure.
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
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
