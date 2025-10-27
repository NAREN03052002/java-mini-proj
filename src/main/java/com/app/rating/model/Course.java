package com.app.rating.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Course, including its associated Professor and calculated ratings.
 */
public class Course {
    private int id;
    private String code;
    private String name;
    private String professor;
    private String semester;
    
    // Aggregated data fields
    private int totalRatings;
    private double avgQuality;
    private double avgAssignments;
    private double avgGrading;
    private double overallRating; 
    
    // List of feedback associated with this course
    private List<Feedback> reviews = new ArrayList<>();

    public Course(int id, String code, String name, String professor, String semester) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.professor = professor;
        this.semester = semester;
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getProfessor() { return professor; }
    public String getSemester() { return semester; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    public double getAvgQuality() { return avgQuality; }
    public void setAvgQuality(double avgQuality) { this.avgQuality = avgQuality; }

    public double getAvgAssignments() { return avgAssignments; }
    public void setAvgAssignments(double avgAssignments) { this.avgAssignments = avgAssignments; }

    public double getAvgGrading() { return avgGrading; }
    public void setAvgGrading(double avgGrading) { this.avgGrading = avgGrading; }

    public double getOverallRating() { return overallRating; }
    public void setOverallRating(double overallRating) { this.overallRating = overallRating; }

    public List<Feedback> getReviews() { return reviews; }
    public void setReviews(List<Feedback> reviews) { this.reviews = reviews; }
}
