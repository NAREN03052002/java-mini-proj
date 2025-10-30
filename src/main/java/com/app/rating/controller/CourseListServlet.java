package com.app.rating.controller;

import com.app.rating.model.Course;
import com.app.rating.service.FeedbackService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException; // CRITICAL: Added import
import java.util.List;
import java.util.Optional;

@WebServlet("/courses")
public class CourseListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FeedbackService service = new FeedbackService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        List<Course> courseList = null;
        Optional<Course> trending = Optional.empty();
        String error = null;

        try {
            // 1. Fetch data from the Service layer (now reads from DB)
            courseList = service.getAllCoursesWithRatings();
            
            // 2. Determine the trending course
            trending = service.getTrendingCourse();
        } catch (SQLException e) {
            error = "Database Error: Could not load courses. Ensure the courses table exists.";
            e.printStackTrace();
        }

        // 3. Set data as request attributes for the JSP
        request.setAttribute("courseList", courseList);
        trending.ifPresent(course -> request.setAttribute("trendingCourse", course));
        if (error != null) {
            request.setAttribute("dbError", error);
        }

        // 4. Forward to the JSP view
        request.getRequestDispatcher("/courses.jsp").forward(request, response);
    }
}
