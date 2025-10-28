package com.app.rating.controller;

import com.app.rating.model.Feedback;
import com.app.rating.model.Question;
import com.app.rating.service.FeedbackService;

// *** REVISED IMPORTS START ***
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// *** REVISED IMPORTS END ***

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/submitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        FeedbackService service = new FeedbackService();
        int courseId = 0;
        
        try {
            // 1. Fetch fixed parameters and all questions
            courseId = Integer.parseInt(request.getParameter("courseId"));
            List<Question> allQuestions = service.getAllQuestions();
            Map<Integer, String> dynamicAnswers = new HashMap<>();
            
            // --- DYNAMIC PARAMETER PROCESSING AND VALIDATION ---
            String validationError = null;

            for (Question q : allQuestions) {
                String paramName;
                if (q.getType().equals("RATING")) {
                    paramName = "rating_" + q.getId();
                } else if (q.getType().equals("TEXT")) {
                    paramName = "text_" + q.getId();
                } else {
                    continue; 
                }
                
                String answer = request.getParameter(paramName);
                
                // Validation Logic
                if (q.isRequired()) {
                    if (answer == null || answer.trim().isEmpty() || 
                       (q.getType().equals("TEXT") && answer.trim().length() < 10) || 
                       (q.getType().equals("RATING") && Integer.parseInt(answer) == 0)) {
                        
                        // Set specific error message for redirect
                        validationError = "Required field missing or invalid: " + q.getText();
                        break; 
                    }
                }
                
                if (answer != null) {
                    dynamicAnswers.put(q.getId(), answer.trim());
                }
            }
            
            // Handle validation failure by redirecting back to the form
            if (validationError != null) {
                response.sendRedirect(request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=" + validationError);
                return;
            }

            // 2. Create the Feedback model object
            Feedback feedback = new Feedback();
            feedback.setCourseId(courseId);
            feedback.setTimestamp(System.currentTimeMillis()); 
            
            // Set the dynamic answers map
            feedback.setAnswers(dynamicAnswers);
            
            // --- BACKWARD COMPATIBILITY FIX ---
            // Manually map the original fixed fields (Q1, Q2, Q3, Q6) for the old calculation logic in FeedbackService
            feedback.setQualityRating(Integer.parseInt(dynamicAnswers.getOrDefault(1, "0")));
            feedback.setAssignmentRating(Integer.parseInt(dynamicAnswers.getOrDefault(2, "0")));
            feedback.setGradingRating(Integer.parseInt(dynamicAnswers.getOrDefault(3, "0")));
            feedback.setReviewText(dynamicAnswers.getOrDefault(6, "No written comment provided."));
            // -----------------------------------

            // 3. Save the feedback
            FeedbackService.addFeedback(feedback);
            
            // 4. POST-Redirect-GET Pattern
            response.sendRedirect(request.getContextPath() + "/courses?success=true");

        } catch (NumberFormatException e) {
            // Catches errors if user bypasses JS validation (e.g., non-integer in a rating field)
            response.sendRedirect(request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Invalid numeric rating value submitted.");
        } catch (Exception e) {
            // General server error fallback
            System.err.println("Error processing feedback: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred during submission.");
        }
    }
}
