package com.app.rating.controller;

import com.app.rating.model.Feedback;
import com.app.rating.service.FeedbackService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/submitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Instantiate the service once (instance variable)
    private FeedbackService service = new FeedbackService(); 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        int courseId = 0;
        
        try {
            // 1. Get and parse fixed form parameters
            courseId = Integer.parseInt(request.getParameter("courseId"));
            int qualityRating = Integer.parseInt(request.getParameter("qualityRating"));
            int assignmentRating = Integer.parseInt(request.getParameter("assignmentRating"));
            int gradingRating = Integer.parseInt(request.getParameter("gradingRating"));
            String reviewText = request.getParameter("reviewText");

            // Basic validation
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
            // *** FIX: Call the method on the service INSTANCE ***
            service.addFeedback(feedback);
            
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
