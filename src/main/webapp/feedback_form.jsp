<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.app.rating.model.Course" %>
<%@ page import="com.app.rating.service.FeedbackService" %>
<%@ page import="java.util.Optional" %>

<%
    // --- Controller Logic (Scriptlet) ---
    int courseId = 0;
    try {
        courseId = Integer.parseInt(request.getParameter("courseId"));
    } catch (NumberFormatException e) {
        response.sendRedirect("courses");
        return;
    }
    
    FeedbackService service = new FeedbackService();

    // 1. Fetch Course Details
    Optional<Course> courseOpt = service.getCourseById(courseId);
    if (!courseOpt.isPresent()) {
        response.sendRedirect("courses");
        return;
    }
    Course course = courseOpt.get();
    request.setAttribute("courseDetail", course);
    
    // NOTE: The logic to fetch questions is REMOVED.
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Feedback</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
        .rating-star { cursor: pointer; transition: color 0.2s; color: #d1d5db; }
        .rating-star.filled { color: #f59e0b; }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center">

    <div class="bg-white rounded-xl shadow-2xl w-full max-w-lg p-8 m-4">
        <h3 class="text-3xl font-bold text-gray-800 mb-1">Rate Course</h3>
        <p class="text-lg text-indigo-600 font-semibold mb-2">${courseDetail.code}: ${courseDetail.name}</p>
        <p class="text-sm text-gray-500 mb-6">Professor: ${courseDetail.professor}</p>

        <c:if test="${not empty param.error}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                <strong class="font-bold">Error!</strong>
                <span class="block sm:inline">${param.error}</span>
            </div>
        </c:if>

        <form action="submitFeedback" method="POST">
            <input type="hidden" name="courseId" value="${courseDetail.id}">

            <div class="space-y-6">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">1. Teaching Quality (1=Poor, 5=Excellent)</label>
                    <div class="flex space-x-1 text-3xl text-gray-300" id="stars-quality">
                        </div>
                    <input type="hidden" id="qualityRating" name="qualityRating" required value="0">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">2. Assignment Load/Relevance (1=Useless, 5=Highly Relevant)</label>
                    <div class="flex space-x-1 text-3xl text-gray-300" id="stars-assignments">
                    </div>
                    <input type="hidden" id="assignmentRating" name="assignmentRating" required value="0">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">3. Grading Fairness (1=Unfair, 5=Very Fair)</label>
                    <div class="flex space-x-1 text-3xl text-gray-300" id="stars-grading">
                    </div>
                    <input type="hidden" id="gradingRating" name="gradingRating" required value="0">
                </div>

                <div>
                    <label for="reviewText" class="block text-sm font-medium text-gray-700 mb-2">4. Written Review (Anonymous)</label>
                    <textarea id="reviewText" name="reviewText" rows="4" minlength="10" required 
                        class="w-full border border-gray-300 rounded-lg p-3 focus:ring-indigo-500 focus:border-indigo-500 shadow-sm" 
                        placeholder="Share your anonymous feedback (min 10 words)"></textarea>
                    <p class="text-xs text-gray-500 mt-1">This review will be public on the course page.</p>
                </div>
            </div>

            <div class="mt-8 flex justify-between space-x-3">
                <a href="courses" class="px-5 py-2.5 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition border">
                    Back to List
                </a>
                <button type="submit" class="px-6 py-2.5 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition shadow-lg">
                    Submit Rating
                </button>
            </div>
        </form>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const ratingParams = [
                { containerId: 'stars-quality', inputId: 'qualityRating' },
                { containerId: 'stars-assignments', inputId: 'assignmentRating' },
                { containerId: 'stars-grading', inputId: 'gradingRating' }
            ];

            // Function to handle star selection visual and data update
            function setRating(container, inputField, value) {
                // Update visual stars
                container.querySelectorAll('.rating-star').forEach(star => {
                    const starValue = parseInt(star.getAttribute('data-value'));
                    if (starValue <= value) {
                        star.classList.add('filled');
                    } else {
                        star.classList.remove('filled');
                    }
                });

                // Update hidden form field value (The critical step)
                inputField.value = value;
            }

            // Initialization loop
            ratingParams.forEach(param => {
                const container = document.getElementById(param.containerId);
                const inputField = document.getElementById(param.inputId);

                for (let i = 1; i <= 5; i++) {
                    const star = document.createElement('span');
                    star.className = 'rating-star';
                    star.textContent = '★';
                    star.setAttribute('data-value', i);
                    
                    // Attach event listener directly
                    star.addEventListener('click', (function(ratingValue) {
                        return function() {
                            setRating(container, inputField, ratingValue);
                        };
                    })(i));

                    container.appendChild(star);
                }
            });
        });
    </script>
</body>
</html>
