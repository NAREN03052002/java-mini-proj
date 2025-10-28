<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Course</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f3f4f6; }
    </style>
</head>
<body class="min-h-screen flex items-center justify-center">

    <div class="bg-white rounded-xl shadow-2xl w-full max-w-md p-8 m-4">
        <h3 class="text-3xl font-bold text-gray-800 mb-1">Create New Course</h3>
        <p class="text-sm text-gray-500 mb-6">Add a subject and professor to the rating system.</p>

        <c:if test="${not empty param.error}">
            <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                <span class="block sm:inline">${param.error}</span>
            </div>
        </c:if>

        <form action="createCourse" method="POST" class="space-y-4">
            <div>
                <label for="courseCode" class="block text-sm font-medium text-gray-700">Course Code (e.g., CS401)</label>
                <input type="text" id="courseCode" name="courseCode" required class="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-indigo-500 focus:border-indigo-500">
            </div>
            <div>
                <label for="courseName" class="block text-sm font-medium text-gray-700">Course Name</label>
                <input type="text" id="courseName" name="courseName" required class="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-indigo-500 focus:border-indigo-500">
            </div>
            <div>
                <label for="professorName" class="block text-sm font-medium text-gray-700">Professor Name</label>
                <input type="text" id="professorName" name="professorName" required class="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-indigo-500 focus:border-indigo-500">
            </div>
             <div>
                <label for="semester" class="block text-sm font-medium text-gray-700">Semester/Term</label>
                <input type="text" id="semester" name="semester" value="Fall 2025" class="mt-1 w-full border border-gray-300 rounded-lg p-2 focus:ring-indigo-500 focus:border-indigo-500">
            </div>
            
            <div class="pt-4 flex justify-between">
                 <a href="courses" class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition border">
                    Cancel
                </a>
                <button type="submit" class="px-6 py-2 text-sm font-medium text-white bg-indigo-600 rounded-lg hover:bg-indigo-700 transition shadow-lg">
                    Add Course
                </button>
            </div>
        </form>
    </div>
</body>
</html>
