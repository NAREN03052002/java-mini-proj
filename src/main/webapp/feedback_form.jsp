<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.app.rating.model.Course" %>
<%@ page import="com.app.rating.model.Question" %>
<%@ page import="com.app.rating.service.FeedbackService" %>
<%@ page import="java.util.List" %>
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
    
    // 2. Fetch Questions from the Service
    List<Question> questionList = service.getAllQuestions();
    request.setAttribute("questionList", questionList);
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

        <form action="submitFeedback" method="POST" id="feedbackForm">
            <input type="hidden" name="courseId" value="${courseDetail.id}">

            <div class="space-y-6" id="questionsContainer">
                <c:forEach var="q" items="${questionList}" varStatus="loop">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">${loop.index + 1}. ${q.text} 
                            <c:if test="${q.required}"><span class="text-red-500">*</span></c:if>
                        </label>
                        
                        <c:choose>
                            <c:when test="${q.type == 'RATING'}">
                                <div class="flex space-x-1 text-3xl text-gray-300 rating-container" 
                                     id="stars-${q.id}" 
                                     data-question-id="${q.id}"
                                     data-current-rating="0"> 
                                    </div>
                                <input type="hidden" id="rating_${q.id}" name="rating_${q.id}" value="">
                            </c:when>
                            <c:when test="${q.type == 'TEXT'}">
                                <textarea name="text_${q.id}" id="text_${q.id}" rows="3" 
                                    class="w-full border border-gray-300 rounded-lg p-3 focus:ring-indigo-500 focus:border-indigo-500 shadow-sm" 
                                    placeholder="Enter your response here"
                                    <c:if test="${q.required}">required minlength="10"</c:if>
                                ></textarea>
                                <c:if test="${!q.required}"><p class="text-xs text-gray-500 mt-1">Optional comment.</p></c:if>
                            </c:when>
                        </c:choose>
                    </div>
                </c:forEach>
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
            const form = document.getElementById('feedbackForm');

            // Function to update visual stars and the DATA attribute
            function setRating(container, ratingValue) {
                // Update visual stars
                container.querySelectorAll('.rating-star').forEach(star => {
                    const starValue = parseInt(star.getAttribute('data-value'));
                    if (starValue <= ratingValue) {
                        star.classList.add('filled');
                    } else {
                        star.classList.remove('filled');
                    }
                });

                // Update the data attribute on the container div (The source of truth)
                container.setAttribute('data-current-rating', ratingValue);
            }

            // Initial Star Rendering & Event Delegation Setup
            const ratingContainers = document.querySelectorAll('.rating-container');
            ratingContainers.forEach(container => {
                for (let i = 1; i <= 5; i++) {
                    const star = document.createElement('span');
                    star.className = 'rating-star';
                    star.textContent = '★';
                    star.setAttribute('data-value', i);
                    container.appendChild(star);
                }
            });
            
            // Attach ONE event listener to the form element for delegation
            form.addEventListener('click', function(event) {
                let target = event.target;
                if (target.classList.contains('rating-star')) {
                    let container = target.closest('.rating-container');
                    if (!container) return; 

                    const ratingValue = parseInt(target.getAttribute('data-value'));
                    setRating(container, ratingValue);
                }
            });


            // *** FINAL GUARANTEE FIX: Intercept submission and manually set required rating values ***
            form.addEventListener('submit', function(event) {
                let ratingMissing = false;

                ratingContainers.forEach(container => {
                    const questionId = container.getAttribute('data-question-id');
                    const hiddenInput = document.getElementById(`rating_${questionId}`);
                    const selectedRating = container.getAttribute('data-current-rating');

                    if (hiddenInput) {
                        // CRITICAL FIX: Force the value from the data attribute into the input field
                        hiddenInput.value = selectedRating;
                        
                        // If the field is required (we check for its existence for simplicity)
                        // and the value is still '0', prevent submission.
                        // NOTE: Server-side validation is still the final authority.
                        if (hiddenInput.hasAttribute('required') && parseInt(selectedRating) === 0) {
                            ratingMissing = true;
                        }
                    }
                });
                
                if (ratingMissing) {
                    // Prevent submission and show native HTML form validation message
                    event.preventDefault();
                    // NOTE: The server redirect will handle the persistent error message using the 'Error!' block.
                }
                
                // If rating is present, the form proceeds and the server handles text validation.
            });
        });
    </script>
</body>
</html>
