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
                
                // *** HARDENED VALIDATION LOGIC START ***
                if (q.isRequired()) {
                    // Check 1: Must not be null/empty for any required field
                    if (answer == null || answer.trim().isEmpty()) {
                        validationError = "Required field missing: " + q.getText();
                        break;
                    }
                    
                    // Check 2: Text length validation
                    if (q.getType().equals("TEXT") && answer.trim().length() < 10) {
                        validationError = "Written review must be at least 10 characters long.";
                        break;
                    }
                    
                    // Check 3: RATING MUST be a number > 0. 
                    if (q.getType().equals("RATING")) {
                        try {
                            if (Integer.parseInt(answer) < 1 || Integer.parseInt(answer) > 5) {
                                // This catches the persistent value of "0" (or less than 1)
                                validationError = "Required rating missing or invalid: " + q.getText();
                                break;
                            }
                        } catch (NumberFormatException nfe) {
                            // This catches cases where the answer is non-numeric
                            validationError = "Invalid numeric rating value submitted for: " + q.getText();
                            break;
                        }
                    }
                }
                // *** HARDENED VALIDATION LOGIC END ***
                
                if (answer != null) {
                    dynamicAnswers.put(q.getId(), answer.trim());
                }
            }
            
            // Handle validation failure by redirecting back to the form
            if (validationError != null) {
                String redirectURL = request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=" + validationError;
                response.sendRedirect(response.encodeRedirectURL(redirectURL));
                return;
            }

            // 2. Create the Feedback model object
            Feedback feedback = new Feedback();
            feedback.setCourseId(courseId);
            feedback.setTimestamp(System.currentTimeMillis()); 
            
            // Set the dynamic answers map
            feedback.setAnswers(dynamicAnswers);
            
            // --- BACKWARD COMPATIBILITY FIX ---
            // Manually map the original fixed fields (Q1, Q2, Q3) and the review text (Q6) 
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
            // Catches errors if courseId parsing failed (rare if link is correct)
            String redirectURL = request.getContextPath() + "/courses?error=Invalid course selection.";
            response.sendRedirect(response.encodeRedirectURL(redirectURL));
        } catch (Exception e) {
            // General server error fallback
            System.err.println("Error processing feedback: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred during submission.");
        }
    }
}
