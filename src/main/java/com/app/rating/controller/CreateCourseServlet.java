package com.app.rating.controller;

import com.app.rating.model.Course;
import com.app.rating.service.FeedbackService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/createCourse")
public class CreateCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
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

            // In a DB-backed app, this would use the next available ID from the DB
            int newId = (int) (System.currentTimeMillis() % 10000); 

            Course newCourse = new Course(newId, code, name, professor, semester);
            
            // Call the service to save the new course
            FeedbackService.addCourse(newCourse);
            
            // Redirect to the main course list
            response.sendRedirect(request.getContextPath() + "/courses?newCourse=true");

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating course: " + e.getMessage());
        }
    }
}
