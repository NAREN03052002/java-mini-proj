package com.app.rating.model;

import java.util.Map;
import java.util.HashMap;

/**
 * Represents a single anonymous feedback submission.
 * Updated to store dynamic answers.
 */
public class Feedback {
    private int id; // Simulates a primary key
    private int courseId;
    
    // *** NEW: Dynamic storage for all custom question answers ***
    private Map<Integer, String> answers = new HashMap<>(); 
    
    // Fixed fields retained for backward compatibility with calculation logic
    private int qualityRating; 
    private int assignmentRating; 
    private int gradingRating; 
    private String reviewText; 
    
    private long timestamp;

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    // *** NEW: Getter and Setter for the dynamic Map ***
    public Map<Integer, String> getAnswers() { return answers; }
    public void setAnswers(Map<Integer, String> answers) { this.answers = answers; }

    public int getQualityRating() { return qualityRating; }
    public void setQualityRating(int qualityRating) { this.qualityRating = qualityRating; }

    public int getAssignmentRating() { return assignmentRating; }
    public void setAssignmentRating(int assignmentRating) { this.assignmentRating = assignmentRating; }

    public int getGradingRating() { return gradingRating; }
    public void setGradingRating(int gradingRating) { this.gradingRating = gradingRating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
