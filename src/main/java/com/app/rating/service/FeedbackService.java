package com.app.rating.service;

import com.app.rating.model.Course;
import com.app.rating.model.Feedback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles business logic, data simulation, and calculation of average ratings.
 */
public class FeedbackService {

    // --- Simulated Data Store (In-Memory replacement for a Database) ---
    private static final List<Course> COURSE_DB = new ArrayList<>();
    private static final List<Feedback> FEEDBACK_DB = new ArrayList<>();
    private static int FEEDBACK_ID_COUNTER = 1;

    // Static initializer to populate initial courses and some mock data
    static {
        // Initial Course Listings
        COURSE_DB.add(new Course(101, "CS201", "Data Structures", "Dr. Elena Rossi", "Fall 2024"));
        COURSE_DB.add(new Course(202, "MA101", "Calculus I", "Prof. John Smith", "Fall 2024"));
        COURSE_DB.add(new Course(303, "HI305", "Modern History", "Dr. Anya Sharma", "Spring 2024"));

        // *** FIX 1: ADD INITIAL MOCK FEEDBACK TO SHOW RATINGS ***
        // Mock Feedback 1
        Feedback f1 = new Feedback(); 
        f1.setCourseId(101); 
        f1.setQualityRating(5); 
        f1.setAssignmentRating(4); 
        f1.setGradingRating(5); 
        f1.setReviewText("Excellent professor, highly recommended."); 
        f1.setTimestamp(System.currentTimeMillis());
        addFeedback(f1); 
        
        // Mock Feedback 2
        Feedback f2 = new Feedback(); 
        f2.setCourseId(101); 
        f2.setQualityRating(4); 
        f2.setAssignmentRating(3); 
        f2.setGradingRating(4); 
        f2.setReviewText("Assignments were a bit heavy but fair grading."); 
        f2.setTimestamp(System.currentTimeMillis() - 10000); // Older timestamp
        addFeedback(f2);
    }
    // -------------------------------------------------------------------

    /**
     * Retrieves all courses and calculates their current average ratings.
     */
    public List<Course> getAllCoursesWithRatings() {
        for (Course c : COURSE_DB) calculateCourseAverages(c);
        return COURSE_DB;
    }

    /**
     * Retrieves a single course by ID.
     * Also calculates averages for the requested course.
     */
    public Optional<Course> getCourseById(int id) {
        Optional<Course> courseOpt = COURSE_DB.stream().filter(c -> c.getId() == id).findFirst();
        // *** FIX 2: Ensure averages are calculated even when fetching a single course ***
        courseOpt.ifPresent(this::calculateCourseAverages); 
        return courseOpt;
    }

    private void calculateCourseAverages(Course course) {
        List<Feedback> reviews = FEEDBACK_DB.stream().filter(f -> f.getCourseId() == course.getId()).collect(Collectors.toList());
        if (reviews.isEmpty()) {
            course.setTotalRatings(0);
            course.setAvgQuality(0.0);
            course.setAvgAssignments(0.0);
            course.setAvgGrading(0.0);
            course.setOverallRating(0.0);
            course.setReviews(new ArrayList<>());
            return;
        }

        double sumQuality = reviews.stream().mapToInt(Feedback::getQualityRating).sum();
        double sumAssignments = reviews.stream().mapToInt(Feedback::getAssignmentRating).sum();
        double sumGrading = reviews.stream().mapToInt(Feedback::getGradingRating).sum();
        int count = reviews.size();
        
        // Calculate and round averages
        double avgQ = Math.round((sumQuality/count)*10.0)/10.0;
        double avgA = Math.round((sumAssignments/count)*10.0)/10.0;
        double avgG = Math.round((sumGrading/count)*10.0)/10.0;
        double overall = Math.round(((avgQ+avgA+avgG)/3.0)*10.0)/10.0;

        course.setTotalRatings(count);
        course.setAvgQuality(avgQ);
        course.setAvgAssignments(avgA);
        course.setAvgGrading(avgG);
        course.setOverallRating(overall);
        
        reviews.sort(Comparator.comparing(Feedback::getTimestamp).reversed());
        course.setReviews(reviews);
    }

    /**
     * Simulates adding a new feedback entry to the database.
     */
    public static void addFeedback(Feedback f) {
        f.setId(FEEDBACK_ID_COUNTER++);
        FEEDBACK_DB.add(f);
    }

    public Optional<Course> getTrendingCourse() {
        return COURSE_DB.stream().peek(this::calculateCourseAverages)
                .filter(c -> c.getTotalRatings() > 0)
                .max(Comparator.comparing(Course::getOverallRating));
    }
}
