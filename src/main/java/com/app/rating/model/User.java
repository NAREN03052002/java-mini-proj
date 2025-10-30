package com.app.rating.model;

public class User {
    private int userId;
    private String username;
    private String passwordHash; // Store hashed password, NOT plaintext
    private String email;

    // Default constructor
    public User() {} 
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
