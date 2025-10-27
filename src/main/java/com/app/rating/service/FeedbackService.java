package com.app.rating.service;

import com.app.rating.model.Course;
import com.app.rating.model.Feedback;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles business logic, data simulation, and calculation of average ratings.
 */
public class FeedbackService {
    private static final List<Course> COURSE_DB = new ArrayList<>();
    private static final List<Feedback> FEEDBACK_DB = new ArrayList<>();
    private static int FEEDBACK_ID_COUNTER = 1;

    static {
        COURSE_DB.add(new Course(101, "CS201", "Data Structures", "Dr. Elena Rossi", "Fall 2024"));
        COURSE_DB.add(new Course(202, "MA101", "Calculus I", "Prof. John Smith", "Fall 2024"));
        COURSE_DB.add(new Course(303, "HI305", "Modern History", "Dr. Anya Sharma", "Spring 2024"));
    }

    public List<Course> getAllCoursesWithRatings() {
        for (Course c : COURSE_DB) calculateCourseAverages(c);
        return COURSE_DB;
    }

    public Optional<Course> getCourseById(int id) {
        return COURSE_DB.stream().filter(c -> c.getId() == id).findFirst();
    }

    private void calculateCourseAverages(Course course) {
        List<Feedback> reviews = FEEDBACK_DB.stream().filter(f -> f.getCourseId() == course.getId()).collect(Collectors.toList());
        if (reviews.isEmpty()) {
            course.setTotalRatings(0);
            course.setAvgQuality(0);
            course.setAvgAssignments(0);
            course.setAvgGrading(0);
            course.setOverallRating(0);
            course.setReviews(new ArrayList<>());
            return;
        }

        double sumQuality = reviews.stream().mapToInt(Feedback::getQualityRating).sum();
        double sumAssignments = reviews.stream().mapToInt(Feedback::getAssignmentRating).sum();
        double sumGrading = reviews.stream().mapToInt(Feedback::getGradingRating).sum();
        int count = reviews.size();
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
