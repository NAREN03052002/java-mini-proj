package com.app.rating.controller;

import com.app.rating.model.Course;
import com.app.rating.service.FeedbackService;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/courses")
public class CourseListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FeedbackService service = new FeedbackService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Course> courseList = service.getAllCoursesWithRatings();
        Optional<Course> trending = service.getTrendingCourse();
        request.setAttribute("courseList", courseList);
        trending.ifPresent(c -> request.setAttribute("trendingCourse", c));
        request.getRequestDispatcher("/courses.jsp").forward(request, response);
    }
}
