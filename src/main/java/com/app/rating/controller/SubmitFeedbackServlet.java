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

        int courseId = 0;
        
        try {
            // 1. Get and parse FIXED form parameters (Matches the reverted Feedback.java and fixed form)
            courseId = Integer.parseInt(request.getParameter("courseId"));
            int qualityRating = Integer.parseInt(request.getParameter("qualityRating"));
            int assignmentRating = Integer.parseInt(request.getParameter("assignmentRating"));
            int gradingRating = Integer.parseInt(request.getParameter("gradingRating"));
            String reviewText = request.getParameter("reviewText");

            // Basic validation (Checks for values 1-5, matching the form structure)
            if (qualityRating < 1 || assignmentRating < 1 || gradingRating < 1) {
                String redirectURL = request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Please provide a rating (1-5 stars) for all three parameters.";
                response.sendRedirect(response.encodeRedirectURL(redirectURL));
                return;
            }
            if (reviewText == null || reviewText.trim().length() < 10) {
                String redirectURL = request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Written review must be at least 10 characters long.";
                response.sendRedirect(response.encodeRedirectURL(redirectURL));
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

            // 3. Save the feedback
            FeedbackService.addFeedback(feedback);
            
            // 4. POST-Redirect-GET Pattern
            response.sendRedirect(request.getContextPath() + "/courses?success=true");

        } catch (NumberFormatException e) {
            String redirectURL = request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Invalid numeric rating value submitted.";
            response.sendRedirect(response.encodeRedirectURL(redirectURL));
        } catch (Exception e) {
            System.err.println("Error processing feedback: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred during submission.");
        }
    }
}
