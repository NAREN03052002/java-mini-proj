package com.app.rating.controller;

import com.app.rating.model.Feedback;
import com.app.rating.service.FeedbackService;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/submitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            int qualityRating = Integer.parseInt(request.getParameter("qualityRating"));
            int assignmentRating = Integer.parseInt(request.getParameter("assignmentRating"));
            int gradingRating = Integer.parseInt(request.getParameter("gradingRating"));
            String reviewText = request.getParameter("reviewText");

            if (reviewText == null || reviewText.trim().length() < 10) {
                response.sendRedirect(request.getContextPath() + "/feedback_form.jsp?courseId=" + courseId + "&error=Review must be at least 10 characters long.");
                return;
            }

            Feedback f = new Feedback();
            f.setCourseId(courseId);
            f.setQualityRating(qualityRating);
            f.setAssignmentRating(assignmentRating);
            f.setGradingRating(gradingRating);
            f.setReviewText(reviewText);
            f.setTimestamp(System.currentTimeMillis());

            FeedbackService.addFeedback(f);
            response.sendRedirect(request.getContextPath() + "/courses?success=true");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
}
