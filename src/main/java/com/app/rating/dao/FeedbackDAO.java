package com.app.rating.dao;

import com.app.rating.model.Feedback;
import com.app.rating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence for Feedback objects.
 */
public class FeedbackDAO {

    public boolean addFeedback(Feedback feedback) throws SQLException {
        // NOTE: We are using fixed parameters for the INSERT query for now, matching the model.
        String sql = "INSERT INTO feedback (course_id, quality_rating, assignment_rating, grading_rating, review_text) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, feedback.getCourseId());
            stmt.setInt(2, feedback.getQualityRating());
            stmt.setInt(3, feedback.getAssignmentRating());
            stmt.setInt(4, feedback.getGradingRating());
            stmt.setString(5, feedback.getReviewText());

            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
    }

    public List<Feedback> getFeedbackByCourseId(int courseId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE course_id = ? ORDER BY submitted_at DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setId(rs.getInt("feedback_id"));
                feedback.setCourseId(rs.getInt("course_id"));
                feedback.setQualityRating(rs.getInt("quality_rating"));
                feedback.setAssignmentRating(rs.getInt("assignment_rating"));
                feedback.setGradingRating(rs.getInt("grading_rating"));
                feedback.setReviewText(rs.getString("review_text"));
                // Timestamp can be set here if needed, but not required for current functionality
                feedbackList.add(feedback);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return feedbackList;
    }
}
