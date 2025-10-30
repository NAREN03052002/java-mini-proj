package com.app.rating.dao;

import com.app.rating.model.User;
import com.app.rating.util.DBConnection;
import com.app.rating.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Hash the password before storing it
            String hashedPassword = PasswordUtil.hashPassword(user.getPasswordHash());

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());

            return stmt.executeUpdate() > 0;
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, email FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }

    public User validateLogin(String username, String password) throws SQLException {
        User user = getUserByUsername(username);
        if (user != null) {
            if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                return user; // Success
            }
        }
        return null; // Failure
    }
}
