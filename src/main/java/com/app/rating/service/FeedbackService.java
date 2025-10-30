package com.app.rating.service;

import com.app.rating.model.Course;
import com.app.rating.model.Feedback;
import com.app.rating.dao.CourseDAO; // NEW: To fetch/save Course data
import com.app.rating.dao.FeedbackDAO; // NEW: To fetch/save Feedback data

import java.sql.SQLException; // CRITICAL: Must be imported to handle DB errors
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles business logic, retrieving data from DAOs, and calculating average ratings.
 * NOTE: All static in-memory lists (COURSE_DB, FEEDBACK_DB) are REMOVED.
 */
public class FeedbackService {

    // Inject DAO dependencies
    private CourseDAO courseDAO = new CourseDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    
    // NOTE: The static initializer block is REMOVED as data is now loaded from the database.

    /**
     * Retrieves all courses from the database and calculates their current average ratings.
     */
    public List<Course> getAllCoursesWithRatings() throws SQLException {
        // Fetch all courses from the DB
        List<Course> courses = courseDAO.getAllCourses();
        
        // Calculate averages for each course (requires feedback data from DB)
        for (Course c : courses) {
            calculateCourseAverages(c);
        }
        return courses;
    }

    /**
     * Retrieves a single course by ID from the database.
     */
    public Optional<Course> getCourseById(int id) throws SQLException {
        // Since CourseDAO does not have a single get method, we fetch all and filter, 
        // ensuring ratings are calculated.
        List<Course> courses = getAllCoursesWithRatings();
        return courses.stream().filter(c -> c.getId() == id).findFirst();
    }
    
    // NOTE: The getAllQuestions() method is REMOVED, as it was part of the failed dynamic upgrade.

    private void calculateCourseAverages(Course course) throws SQLException {
        // Fetch all feedback for this specific course from the DB
        List<Feedback> reviews = feedbackDAO.getFeedbackByCourseId(course.getId());
        
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
        
        // Sorting is done in the DAO, but we keep the sorting call here just in case.
        reviews.sort(Comparator.comparing(Feedback::getTimestamp).reversed());
        course.setReviews(reviews);
    }

    /**
     * Saves a new feedback entry via the FeedbackDAO.
     */
    public boolean addFeedback(Feedback feedback) throws SQLException {
        // Note: The static addFeedback() is replaced with this instance method using the DAO.
        return feedbackDAO.addFeedback(feedback);
    }

    /**
     * Saves a new course via the CourseDAO.
     */
    public Course addCourse(Course newCourse) throws SQLException {
        // Note: The static addCourse() is replaced with this instance method using the DAO.
        return courseDAO.addCourse(newCourse);
    }
    
    public Optional<Course> getTrendingCourse() throws SQLException {
        List<Course> courses = getAllCoursesWithRatings();
        return courses.stream()
                .filter(c -> c.getTotalRatings() > 0)
                .max(Comparator.comparing(Course::getOverallRating));
    }
}
