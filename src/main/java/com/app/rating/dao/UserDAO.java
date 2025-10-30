package com.app.rating.dao;

import com.app.rating.model.User;
import com.app.rating.util.DBConnection;
import com.app.rating.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles data access operations for the 'users' table, with robust error handling.
 */
public class UserDAO {

    public boolean registerUser(User user) throws SQLException {
        // NOTE: The user object MUST contain the hashed password when passed to this method.
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            // The model already contains the hash, set by the SignupServlet
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash()); // Use the pre-hashed value
            stmt.setString(3, user.getEmail());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("SQL Error during user registration:");
            e.printStackTrace();
            throw e; 
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email FROM users WHERE username = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, username);

            rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setEmail(rs.getString("email"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error during user lookup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Ensure resources are closed in reverse order
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public User validateLogin(String username, String password) throws SQLException {
        User user = getUserByUsername(username);
        if (user != null) {
            // CRITICAL CHECK: Use the PasswordUtil to check the plaintext password against the stored hash
            if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                return user; // Success
            }
        }
        return null; // Failure
    }
}
