package com.example.pdfuploader

// Course class
data class Course(val courseCode: String, val courseName: String, val units: List<CourseUnit>)

//
//// Course years
//data class CourseYear(val yearName: String, val units: List<CourseUnit>)

// Course Unit class
data class CourseUnit(val unitName: String, val notesFileName: String, val pastPaperFileName: String)
