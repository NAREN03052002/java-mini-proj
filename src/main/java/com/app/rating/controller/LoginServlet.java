package com.app.rating.controller;

import com.app.rating.model.User;
import com.app.rating.dao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect("login.jsp?error=Username and password are required.");
            return;
        }

        try {
            // 1. Validate credentials against the database
            User user = userDAO.validateLogin(username, password);

            if (user != null) {
                // 2. Successful Login: Create a Session
                HttpSession session = request.getSession();
                session.setAttribute("user", user); // Store the User object in session
                session.setAttribute("isLoggedIn", true);

                // 3. Redirect to the main course list
                response.sendRedirect(request.getContextPath() + "/courses");
            } else {
                // 4. Failed Login
                response.sendRedirect("login.jsp?error=Invalid username or password.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=Database error during login.");
        }
    }
}
