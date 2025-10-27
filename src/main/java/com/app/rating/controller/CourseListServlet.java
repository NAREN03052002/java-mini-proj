package com.app.rating.controller;

import com.app.rating.model.Course;
import com.app.rating.service.FeedbackService;

// *** REVISED IMPORTS START ***
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// *** REVISED IMPORTS END ***

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/courses")
public class CourseListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FeedbackService service = new FeedbackService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // 1. Fetch data from the Service layer (including calculated averages)
        List<Course> courseList = service.getAllCoursesWithRatings();
        
        // 2. Determine the trending course
        Optional<Course> trending = service.getTrendingCourse();

        // 3. Set data as request attributes for the JSP
        request.setAttribute("courseList", courseList);
        trending.ifPresent(course -> request.setAttribute("trendingCourse", course));

        // 4. Forward to the JSP view
        request.getRequestDispatcher("/courses.jsp").forward(request, response);
    }
}
