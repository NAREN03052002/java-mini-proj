package com.app.rating.controller;

import com.app.rating.model.Course;
import com.app.rating.service.FeedbackService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/createCourse")
public class CreateCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // *** FIX: Instantiate the service once ***
    private FeedbackService service = new FeedbackService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            String code = request.getParameter("courseCode").trim();
            String name = request.getParameter("courseName").trim();
            String professor = request.getParameter("professorName").trim();
            String semester = request.getParameter("semester").trim();

            if (code.isEmpty() || name.isEmpty() || professor.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/create_course_form.jsp?error=All fields are required.");
                return;
            }

            // In a DB-backed app, we let the DB generate the ID (pass ID 0 or ignore it).
            Course newCourse = new Course(0, code, name, professor, semester);
            
            // *** FIX: Call the non-static method using the service instance ***
            service.addCourse(newCourse);
            
            // Redirect to the main course list
            response.sendRedirect(request.getContextPath() + "/courses?newCourse=true");

        } catch (SQLException e) {
            System.err.println("SQL Error during course creation: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/create_course_form.jsp?error=Database error: Check server logs for details.");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating course: " + e.getMessage());
        }
    }
}
