<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UniRate - Course & Professor Ratings</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap');
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
        .rating-star { color: #f59e0b; }
    </style>
</head>
<body class="min-h-screen">

    <header class="bg-indigo-700 text-white shadow-lg">
        <div class="container mx-auto px-4 py-4 flex justify-between items-center">
            <h1 class="text-3xl font-extrabold tracking-tight">UniRate System</h1>
            <c:if test="${not empty trendingCourse}">
                <div class="text-sm bg-indigo-800 py-1.5 px-4 rounded-full shadow-md hidden sm:block">
                    <span class="font-semibold mr-2">📈 Trending:</span>
                    ${trendingCourse.code} (<fmt:formatNumber value="${trendingCourse.overallRating}" pattern="0.0"/> / 5)
                </div>
            </c:if>
        </div>
    </header>

    <main class="container mx-auto px-4 py-10">
        <h2 class="text-4xl font-bold text-gray-800 mb-8 border-b pb-3">All Course Feedback</h2>

        <c:if test="${param.success == 'true'}">
            <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-6" role="alert">
                <strong class="font-bold">Success!</strong>
                <span class="block sm:inline">Your anonymous feedback has been recorded.</span>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty courseList}">
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                    <c:forEach var="course" items="${courseList}">
                        <div class="bg-white rounded-xl shadow-lg hover:shadow-xl transition duration-300 overflow-hidden border border-gray-200">
                            <div class="p-6">
                                <div class="flex justify-between items-start mb-2">
                                    <span class="text-sm font-semibold text-indigo-700 bg-indigo-100 py-1 px-3 rounded-full">${course.code} | ${course.semester}</span>
                                    <span class="text-xs text-gray-500 mt-1">${course.totalRatings} Review(s)</span>
                                </div>
                                
                                <h3 class="text-2xl font-bold text-gray-900 mb-2">${course.name}</h3>
                                <p class="text-gray-600 mb-4">Professor: <span class="font-medium">${course.professor}</span></p>

                                <div class="flex items-center space-x-3 mb-4 border-t pt-4">
                                    <div class="text-4xl font-extrabold 
                                        <c:choose>
                                            <c:when test="${course.overallRating >= 4}">text-green-600</c:when>
                                            <c:when test="${course.overallRating >= 3}">text-yellow-600</c:when>
                                            <c:otherwise>text-red-600</c:otherwise>
                                        </c:choose>
                                    ">
                                        <fmt:formatNumber value="${course.overallRating}" pattern="0.0"/>
                                    </div>
                                    <div class="flex flex-col">
                                        <c:set var="stars" value="${course.overallRating}" />
                                        <div class="text-2xl">
                                            <c:forEach begin="1" end="5" var="i">
                                                <c:choose>
                                                    <c:when test="${i <= stars}">
                                                        <span class="rating-star">★</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-gray-300">★</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                        <span class="text-sm text-gray-500">Overall Score</span>
                                    </div>
                                </div>

                                <div class="text-sm space-y-1 mt-4">
                                    <p class="flex justify-between items-center"><span class="text-gray-700">Teaching Quality:</span> <span class="font-semibold text-gray-900"><fmt:formatNumber value="${course.avgQuality}" pattern="0.0"/></span></p>
                                    <p class="flex justify-between items-center"><span class="text-gray-700">Assignments Load:</span> <span class="font-semibold text-gray-900"><fmt:formatNumber value="${course.avgAssignments}" pattern="0.0"/></span></p>
                                    <p class="flex justify-between items-center"><span class="text-gray-700">Grading Fairness:</span> <span class="font-semibold text-gray-900"><fmt:formatNumber value="${course.avgGrading}" pattern="0.0"/></span></p>
                                </div>

                                <div class="mt-6 space-y-3">
                                    <a href="feedback_form.jsp?courseId=${course.id}" 
                                        class="block w-full text-center bg-indigo-600 text-white py-2.5 rounded-lg text-sm font-semibold hover:bg-indigo-700 transition shadow-md">
                                        Leave Anonymous Feedback
                                    </a>
                                    
                                    <c:if test="${course.totalRatings > 0}">
                                        <details class="group">
                                            <summary class="block w-full text-center border border-indigo-500 text-indigo-600 py-2.5 rounded-lg text-sm font-semibold cursor-pointer hover:bg-indigo-50 transition">
                                                View ${course.totalRatings} Reviews
                                            </summary>
                                            <div class="mt-4 max-h-40 overflow-y-auto bg-gray-50 p-3 rounded-lg border">
                                                <c:forEach var="review" items="${course.reviews}" end="2">
                                                    <div class="p-3 mb-2 bg-white rounded shadow-sm border border-gray-100">
                                                        <p class="text-sm font-medium text-gray-800 italic truncate">
                                                            "${review.reviewText}"
                                                        </p>
                                                        <span class="text-xs text-indigo-500">Q:${review.qualityRating} | A:${review.assignmentRating} | G:${review.gradingRating}</span>
                                                    </div>
                                                </c:forEach>
                                                <c:if test="${course.reviews.size() > 3}">
                                                    <p class="text-xs text-center text-gray-500 mt-2">...and ${course.reviews.size() - 3} more reviews.</p>
                                                </c:if>
                                            </div>
                                        </details>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center p-12 border-4 border-dashed border-gray-300 rounded-xl bg-white">
                    <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
                        <path vector-effect="non-scaling-stroke" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 13h6m-3-3v6m-9 7h16a2 2 0 002-2V5a2 2 0 00-2-2H3a2 2 0 00-2 2v14a2 2 0 002 2z" />
                    </svg>
                    <h3 class="mt-2 text-sm font-semibold text-gray-900">No Courses Available</h3>
                    <p class="mt-1 text-sm text-gray-500">The service layer did not return any courses. Check the FeedbackService initialization.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</body>
</html>
