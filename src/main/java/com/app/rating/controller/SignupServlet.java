package com.app.rating.controller;

import com.app.rating.model.User;
import com.app.rating.dao.UserDAO;
import com.app.rating.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
// *** FIX: ADD MISSING IMPORT ***
import java.sql.SQLException; 

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Basic Server-Side Validation
        if (username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty()) {
            response.sendRedirect("signup.jsp?error=All fields are required.");
            return;
        }

        try {
            // Check if user already exists
            if (userDAO.getUserByUsername(username) != null) {
                response.sendRedirect("signup.jsp?error=Username already taken.");
                return;
            }
            
            // 1. Hash the password
            String hashedPassword = PasswordUtil.hashPassword(password);

            // 2. Create User Model
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPasswordHash(hashedPassword); // Store the hash

            // 3. Register user via DAO
            if (userDAO.registerUser(newUser)) {
                // Redirect to login page with success message
                response.sendRedirect("login.jsp?success=registered");
            } else {
                response.sendRedirect("signup.jsp?error=Registration failed due to database issue.");
            }

        } catch (SQLException e) { // Catches the SQLException from the DAO layer
            e.printStackTrace();
            response.sendRedirect("signup.jsp?error=Database error during registration.");
        }
    }
}
