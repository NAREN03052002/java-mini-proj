package com.app.rating.dao;

import com.app.rating.model.Course;
import com.app.rating.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence for Course objects.
 */
public class CourseDAO {

    public Course addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (code, name, professor, semester) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
            
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setString(3, course.getProfessor());
            stmt.setString(4, course.getSemester());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    // *** FIX: Use the new setId method ***
                    course.setId(rs.getInt(1)); 
                    return course;
                }
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return null; 
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, code, name, professor, semester FROM courses ORDER BY course_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("professor"),
                    rs.getString("semester")
                );
                courses.add(course);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignore */ }
            DBConnection.closeConnection(conn);
        }
        return courses;
    }
}
