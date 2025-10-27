package com.app.rating.controller;

import com.app.rating.model.Feedback;
import com.app.rating.service.FeedbackService;

// *** REVISED IMPORTS START ***
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// *** REVISED IMPORTS END ***

import java.io.IOException;

@WebServlet("/submitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {
            // 1. Get and parse form parameters
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int qualityRating = Integer.parseInt(request.getParameter("qualityRating"));
            int assignmentRating = Integer.parseInt(request.getParameter("assignmentRating"));
            int gradingRating = Integer.parseInt(request.getParameter("gradingRating"));
            String reviewText = request.getParameter("reviewText");

            // Basic validation
            if (reviewText == null || reviewText.trim().length() < 10) {
                // If validation fails, redirect back with an error message
                response.sendRedirect(request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Review must be at least 10 characters long.");
                return;
            }

            // 2. Create the Feedback model object
            Feedback feedback = new Feedback();
            feedback.setCourseId(courseId);
            feedback.setQualityRating(qualityRating);
            feedback.setAssignmentRating(assignmentRating);
            feedback.setGradingRating(gradingRating);
            feedback.setReviewText(reviewText);
            feedback.setTimestamp(System.currentTimeMillis()); 

            // 3. Save the feedback (Service layer handles the simulated persistence)
            FeedbackService.addFeedback(feedback);
            
            // 4. POST-Redirect-GET Pattern: Redirect back to the course list page
            response.sendRedirect(request.getContextPath() + "/courses?success=true");

        } catch (NumberFormatException e) {
            // Handle invalid input data
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid rating parameters.");
        } catch (Exception e) {
            // Handle other exceptions (e.g., database error)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred during submission.");
        }
    }
}
